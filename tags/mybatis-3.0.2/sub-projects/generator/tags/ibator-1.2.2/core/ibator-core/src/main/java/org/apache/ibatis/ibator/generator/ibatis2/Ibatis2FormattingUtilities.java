/*
 *  Copyright 2009 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.ibatis.ibator.generator.ibatis2;

import java.util.StringTokenizer;

import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.internal.util.StringUtility;

/**
 * @author Jeff Butler
 *
 */
public class Ibatis2FormattingUtilities {
    /**
     * Utility class - no instances
     */
    private Ibatis2FormattingUtilities() {
        super();
    }

    public static String getEscapedColumnName(IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append(escapeStringForIbatis2(introspectedColumn.getActualColumnName()));
        
        if (introspectedColumn.isColumnNameDelimited()) {
            sb.insert(0, introspectedColumn.getIbatorContext().getBeginningDelimiter());
            sb.append(introspectedColumn.getIbatorContext().getEndingDelimiter());
        }
        
        return sb.toString();
    }

    /**
     * Calculates the string to use in select phrases in SqlMaps.
     * 
     * @return the aliased escaped column name
     */
    public static String getAliasedEscapedColumnName(IntrospectedColumn introspectedColumn) {
        if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
            StringBuilder sb = new StringBuilder();
            
            sb.append(introspectedColumn.getTableAlias());
            sb.append('.');
            sb.append(getEscapedColumnName(introspectedColumn));
            return sb.toString();
        } else {
            return getEscapedColumnName(introspectedColumn);
        }
    }

    public static String getParameterClause(IntrospectedColumn introspectedColumn) {
        return getParameterClause(introspectedColumn, null);
    }
    
    public static String getParameterClause(IntrospectedColumn introspectedColumn, String prefix) {
        StringBuilder sb = new StringBuilder();
        
        sb.append('#');
        sb.append(introspectedColumn.getJavaProperty(prefix));
        
        if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append(",jdbcType="); //$NON-NLS-1$
            sb.append(introspectedColumn.getJdbcTypeName());
            sb.append(",handler="); //$NON-NLS-1$
            sb.append(introspectedColumn.getTypeHandler());
        } else {
            sb.append(':');
            sb.append(introspectedColumn.getJdbcTypeName());
        }
        
        sb.append('#');
        
        return sb.toString();
    }

    /**
     * The phrase to use in a select list.  If there
     * is a table alias, the value will be 
     * "alias.columnName as alias_columnName"
     * 
     * @return the proper phrase
     */
    public static String getSelectListPhrase(IntrospectedColumn introspectedColumn) {
        if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
            StringBuilder sb = new StringBuilder();
            
            sb.append(getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" as "); //$NON-NLS-1$
            if (introspectedColumn.isColumnNameDelimited()) {
                sb.append(introspectedColumn.getIbatorContext().getBeginningDelimiter());
            }
            sb.append(introspectedColumn.getTableAlias());
            sb.append('_');
            sb.append(escapeStringForIbatis2(introspectedColumn.getActualColumnName()));
            if (introspectedColumn.isColumnNameDelimited()) {
                sb.append(introspectedColumn.getIbatorContext().getEndingDelimiter());
            }
            return sb.toString();
        } else {
            return getEscapedColumnName(introspectedColumn);
        }
    }
    
    public static String escapeStringForIbatis2(String s) {
        StringTokenizer st = new StringTokenizer(s, "$#", true); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("$".equals(token)) { //$NON-NLS-1$
                sb.append("$$"); //$NON-NLS-1$
            } else if ("#".equals(token)) { //$NON-NLS-1$
                sb.append("##"); //$NON-NLS-1$
            } else {
                sb.append(token);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * The aliased column name for a select statement generated by the example clauses.
     * This is not appropriate for selects in SqlMaps because the column is
     * not escaped for iBATIS.  If there
     * is a table alias, the value will be alias.columnName.
     * 
     * This method is used in the Example classes and the returned value will be
     * in a Java string.  So we need to escape double quotes if they are
     * the delimiters.
     * 
     * @return the aliased column name
     */
    public static String getAliasedActualColumnName(IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
            sb.append(introspectedColumn.getTableAlias());
            sb.append('.');
        }

        if (introspectedColumn.isColumnNameDelimited()) {
            sb.append(StringUtility.escapeStringForJava(introspectedColumn.getIbatorContext().getBeginningDelimiter()));
        }
        
        sb.append(introspectedColumn.getActualColumnName());
            
        if (introspectedColumn.isColumnNameDelimited()) {
            sb.append(StringUtility.escapeStringForJava(introspectedColumn.getIbatorContext().getEndingDelimiter()));
        }
        
        return sb.toString();
    }

    /**
     * The renamed column name for a select statement.  If there
     * is a table alias, the value will be alias_columnName.  This is
     * appropriate for use in a result map.
     * 
     * @return the renamed column name
     */
    public static String getRenamedColumnNameForResultMap(IntrospectedColumn introspectedColumn) {
        if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
            StringBuilder sb = new StringBuilder();
            
            sb.append(introspectedColumn.getTableAlias());
            sb.append('_');
            sb.append(introspectedColumn.getActualColumnName());
            return sb.toString();
        } else {
            return introspectedColumn.getActualColumnName();
        }
    }
}
