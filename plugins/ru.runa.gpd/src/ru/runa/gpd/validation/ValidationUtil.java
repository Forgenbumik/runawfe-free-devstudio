package ru.runa.gpd.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import ru.runa.gpd.PluginLogger;
import ru.runa.gpd.form.FormType;
import ru.runa.gpd.form.FormTypeProvider;
import ru.runa.gpd.form.FormVariableAccess;
import ru.runa.gpd.lang.model.FormNode;
import ru.runa.gpd.lang.model.Variable;
import ru.runa.gpd.util.IOUtils;
import ru.runa.gpd.util.WorkspaceOperations;
import ru.runa.wfe.var.format.DateFormat;
import ru.runa.wfe.var.format.TimeFormat;

public class ValidationUtil {

    public static List<ValidatorDefinition> getFieldValidatorDefinitions(Variable variable) {
        List<ValidatorDefinition> result = new ArrayList<ValidatorDefinition>();
        for (ValidatorDefinition definition : ValidatorDefinitionRegistry.getValidatorDefinitions().values()) {
            if (!definition.isGlobal() && definition.isApplicable(variable.getJavaClassName())) {
                if (DateFormat.class.getName().equals(variable.getFormat()) && "time".equals(definition.getName())) {
                    continue;
                }
                if (TimeFormat.class.getName().equals(variable.getFormat()) && definition.getName().startsWith("date")) {
                    continue;
                }
                result.add(definition);
            }
        }
        return result;
    }

    public static FormNodeValidation getInitialFormValidation(IFile adjacentFile, FormNode formNode) throws Exception {
        FormNodeValidation validation = new FormNodeValidation();
        IFile formFile = IOUtils.getAdjacentFile(adjacentFile, formNode.getFormFileName());
        if (formFile.exists()) {
            FormType formType = FormTypeProvider.getFormType(formNode.getFormType());
            byte[] formData = IOUtils.readStreamAsBytes(formFile.getContents(true));
            Map<String, FormVariableAccess> formVariables = formType.getFormVariableNames(formNode, formData);
            List<String> variableNames = formNode.getProcessDefinition().getVariableNames(true);
            for (Map.Entry<String, FormVariableAccess> entry : formVariables.entrySet()) {
                if (entry.getValue() == FormVariableAccess.WRITE && variableNames.contains(entry.getKey())) {
                    validation.addFieldEmptyConfigs(entry.getKey());
                }
            }
        }
        return validation;
    }

    public static IFile createNewValidationUsingForm(IFile adjacentFile, FormNode formNode) throws Exception {
        FormNodeValidation validation = getInitialFormValidation(adjacentFile, formNode);
        return rewriteValidation(adjacentFile, formNode, validation);
    }

    public static void updateValidation(IFile adjacentFile, FormNode formNode) throws Exception {
        boolean changed = false;
        FormNodeValidation validation = formNode.getValidation(adjacentFile);

        IFile formFile = IOUtils.getAdjacentFile(adjacentFile, formNode.getFormFileName());
        FormType formType = FormTypeProvider.getFormType(formNode.getFormType());
        byte[] formData = IOUtils.readStreamAsBytes(formFile.getContents(true));
        Map<String, FormVariableAccess> formVariables = formType.getFormVariableNames(formNode, formData);

        FormNodeValidation newValidation = getInitialFormValidation(adjacentFile, formNode);
        Collection<String> variablesNames = validation.getVariableNames();
        List<String> missingVariableNames = new ArrayList<String>();
        for (String variableName : variablesNames) {
            if (!(newValidation.getVariableNames().contains(variableName) || formVariables.get(variableName) == FormVariableAccess.DOUBTFUL)) {
                missingVariableNames.add(variableName);
                changed = true;
            }
        }
        variablesNames.removeAll(missingVariableNames);

        for (String variableName : newValidation.getVariableNames()) {
            if (!variablesNames.contains(variableName)) {
                validation.addFieldConfigs(variableName, newValidation.getFieldConfigs().get(variableName));
                changed = true;
            }
        }
        if (changed) {
            rewriteValidation(adjacentFile, formNode, validation);
            formNode.setDirty();
        }
    }

    public static IFile createEmptyValidation(IFile adjacentFile, FormNode formNode) {
        return rewriteValidation(adjacentFile, formNode, new FormNodeValidation());
    }

    public static IFile rewriteValidation(IFile file, FormNode formNode, FormNodeValidation validation) {
        IFile validationFile = IOUtils.getAdjacentFile(file, formNode.getValidationFileName());
        ValidatorParser.writeValidation(validationFile, formNode, validation);
        return validationFile;
    }

    public static void removeEmptyConfigsForDeletedVariables(IFile validationFile, FormNode formNode, FormNodeValidation validation) {
        Set<String> missedVariableNames = new HashSet<>();
        missedVariableNames.addAll(validation.getVariableNamesWithEmptyConfigs());
        missedVariableNames.removeAll(formNode.getVariableNames(true));
        for (String variableName : missedVariableNames) {
            validation.removeFieldConfigs(variableName);
        }
        ValidatorParser.writeValidation(validationFile, formNode, validation);
    }

    public static void removeValidationIfEmpty(IFile validationFile, FormNodeValidation validation) {
        if (validation.getVariableNames().isEmpty()) {
            try {
                validationFile.setSessionProperty(WorkspaceOperations.PROPERTY_FILE_WILL_BE_DELETED_SHORTLY, Boolean.TRUE);
            } catch (CoreException e) {
                PluginLogger.logError(e);
            }
            WorkspaceOperations.job("Validation editor disposing", (p) -> {
                try {
                    validationFile.delete(true, null);
                } catch (CoreException e) {
                    PluginLogger.logError(e);
                }
            });
        }
    }
}
