package ru.runa.gpd.ui.custom;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

public class SqlHighlightTextStyling extends HighlightTextStyling {

    private static final Color COLOR_KEYWORD = new Color(null, 0, 0, 255);
    private static final Color COLOR_STRING = new Color(null, 128, 128, 128);
    private static final Color COLOR_COMMENT = new Color(null, 0, 128, 0);
    private static final Color COLOR_QUESTION_MARK = new Color(null, 255, 0, 0);

    private static List<RegexpHighlight> highlightDefinitions = new ArrayList<RegexpHighlight>();
    static {
        addHighlight(
                "keyword",
                "(?im)\\b(?:abs|absolute|access|acos|add|add_months|adddate|admin|after|aggregate|all|allocate|alter|and|any|app_name|are|array|as|asc|ascii|asin|assertion|at|atan|atn2|audit|authid|authorization|autonomous_transaction|avg|before|begin|benchmark|between|bfilename|bigint|bin|binary|binary_checksum|binary_integer|bit|bit_count|bit_and|bit_or|blob|body|boolean|both|breadth|bulk|by|call|cascade|cascaded|case|cast|catalog|ceil|ceiling|char|char_base|character|charindex|chartorowid|check|checksum|checksum_agg|chr|class|clob|close|cluster|coalesce|col_length|col_name|collate|collation|collect|column|comment|commit|completion|compress|concat|concat_ws|connect|connection|constant|constraint|constraints|constructorcreate|contains|containsable|continue|conv|convert|corr|corresponding|cos|cot|count|count_big|covar_pop|covar_samp|create|cross|cube|cume_dist|current|current_date|current_path|current_role|current_time|current_timestamp|current_user|currval|cursor|cycle|data|datalength|databasepropertyex|date|date_add|date_format|date_sub|dateadd|datediff|datename|datepart|datetime|day|db_id|db_name|deallocate|dec|declare|decimal|decode|default|deferrable|deferred|degrees|delete|dense_rank|depth|deref|desc|describe|descriptor|destroy|destructor|deterministic|diagnostics|dictionary|disconnect|difference|distinct|do|domain|double|drop|dump|dynamic|each|else|elsif|empth|encode|encrypt|end|end-exec|equals|escape|every|except|exception|exclusive|exec|execute|exists|exit|exp|export_set|extends|external|extract|false|fetch|first|first_value|file|float|floor|file_id|file_name|filegroup_id|filegroup_name|filegroupproperty|fileproperty|for|forall|foreign|format|formatmessage|found|freetexttable|from|from_days|fulltextcatalog|fulltextservice|function|general|get|get_lock|getdate|getansinull|getutcdate|global|go|goto|grant|greatest|group|grouping|having|heap|hex|hextoraw|host|host_id|host_name|hour|ident_incr|ident_seed|ident_current|identified|identity|if|ifnull|ignore|immediate|in|increment|index|index_col|indexproperty|indicator|initcap|initial|initialize|initially|inner|inout|input|insert|instr|instrb|int|integer|interface|intersect|interval|into|is|is_member|is_srvrolemember|is_null|is_numeric|isdate|isnull|isolation|iterate|java|join|key|lag|language|large|last|last_day|last_value|lateral|lcase|lead|leading|least|left|len|length|lengthb|less|level|like|limit|limited|ln|lpad|local|localtime|localtimestamp|locator|lock|log|log10|long|loop|lower|ltrim|make_ref|map|match|max|maxextents|mid|min|minus|minute|mlslabel|mod|mode|modifies|modify|module|month|months_between|names|national|natural|naturaln|nchar|nclob|new|new_time|newid|next|next_day|nextval|no|noaudit|nocompress|nocopy|none|not|nowait|null|nullif|number|number_base|numeric|nvl|nvl2|object|object_id|object_name|object_property|ocirowid|oct|of|off|offline|old|on|online|only|opaque|open|operator|operation|option|or|ord|order|ordinalityorganization|others|out|outer|output|package|pad|parameter|parameters|partial|partition|path|pctfree|percent_rank|pi|pls_integer|positive|positiven|postfix|pow|power|pragma|precision|prefix|preorder|prepare|preserve|primary|prior|private|privileges|procedure|public|radians|raise|rand|range|rank|ratio_to_export|raw|rawtohex|read|reads|real|record|recursive|ref|references|referencing|reftohex|relative|release|release_lock|rename|repeat|replace|resource|restrict|result|return|returns|reverse|revoke|right|rollback|rollup|round|routine|row|row_number|rowid|rowidtochar|rowlabel|rownum|rows|rowtype|rpad|rtrim|savepoint|schema|scroll|scope|search|second|section|seddev_samp|select|separate|sequence|session|session_user|set|sets|share|sign|sin|sinh|size|smallint|some|soundex|space|specific|specifictype|sql|sqlcode|sqlerrm|sqlexception|sqlstate|sqlwarning|sqrt|start|state|statement|static|std|stddev|stdev_pop|strcmp|structure|subdate|substr|substrb|substring|substring_index|subtype|successful|sum|synonym|sys_context|sys_guid|sysdate|system_user|table|tan|tanh|temporary|terminate|than|then|time|timestamp|timezone_abbr|timezone_minute|timezone_hour|timezone_region|tinyint|to|to_char|to_date|to_days|to_number|to_single_byte|trailing|transaction|translate|translation|treat|trigger|trim|true|trunc|truncate|type|ucase|uid|under|union|unique|unknown|unnest|update|upper|usage|use|user|userenv|using|validate|value|values|var_pop|var_samp|varbinary|varchar|varchar2|variable|variance|varying|view|vsize|when|whenever|where|with|without|while|with|work|write|year|zone)\\b",
                COLOR_KEYWORD, SWT.BOLD);
        addHighlight("string", "(?m)\'[^\']*\'", COLOR_STRING, SWT.BOLD);
        addHighlight("lineComment", "(?m)--.*$", COLOR_COMMENT, SWT.ITALIC);
        addHighlight("blockComment", "(?s)/\\*.*?(?:\\*/|\\z)", COLOR_COMMENT, SWT.ITALIC);
        addHighlight("questionMark", "\\?", COLOR_QUESTION_MARK, SWT.BOLD);
    }

    public static void addHighlight(String name, String regexp, Color fg, int textStyle) {
        StyleRange styleRange = new StyleRange(0, 0, fg, null, textStyle);
        highlightDefinitions.add(new RegexpHighlight(name, regexp, false, styleRange));
    }

    public SqlHighlightTextStyling() {
        super(highlightDefinitions);
    }

}
