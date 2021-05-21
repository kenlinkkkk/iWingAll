<%@ page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@ page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@ page import="com.ligerdev.appbase.utils.db.GenDbSource" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
    String poolName = getEmptyIfInputBlank(request.getParameter("pool"), BaseDAO.POOL_NAME_MAIN);
    String tbName = getEmptyIfInputBlank(request.getParameter("table"));
    // String sql = getEmptyIfInputBlank(request.getParameter("sql"), "select * from " + tbName + " where 1 = 0");
    String sql = getEmptyIfInputBlank(request.getParameter("sql"), "");
    String key = getEmptyIfInputBlank(request.getParameter("key"));
    String packageName = getEmptyIfInputBlank(request.getParameter("package"));
    String method = getEmptyIfInputBlank(request.getParameter("method"));
    String rs = genBean(poolName, sql, tbName, key, packageName, method);  
%>
<%! 
    public String genBean(String poolName, String sql, String tbName, String key, String packageName, String method) {
        String rs = "";
        //===============Test===============
        /* 		
        rs += poolName + " - " + sql + " - " + tbName + " - " + key + " - " + packageName + " - " + method;
        rs = GenDbSource.genDTO(poolName == null ? BaseDAO.POOL_NAME_MAIN : poolName,
               "subscribers", !BaseUtils.isBlank("") ? null : "select * from his_201709",
               key == null ? "" : key,
               packageName == null ? "" : packageName);
        */
        //===============End===============
        
        if (BaseUtils.isBlankAll(tbName, sql)) {
            return "\"table\" or \"sql\" is required!";
            
        } else if(BaseUtils.isNotBlank(sql)){
            boolean checkExistTable = BaseDAO.getInstance(poolName).isValidSql("CheckTbl", sql);
            if (!checkExistTable)
                return "table by sql not exist!"; 
        }
        if (BaseUtils.isNotBlank(tbName) && BaseUtils.isBlank(sql)) {
            String sqlCheckMo = "select 1 from " + tbName + " where 1 = 0";
            boolean checkExistTable = BaseDAO.getInstance(poolName).isValidSql("CheckTbl", sqlCheckMo);
            
            if (!checkExistTable)
                return "table not exist!";
        }
        if ("DTO".equalsIgnoreCase(method)) {
            rs = GenDbSource.genDTO(poolName,
                    tbName, BaseUtils.isBlank(sql) ? null : sql,
                    key == null ? "" : key,
                    packageName == null ? "" : packageName);
        }
        if ("DAO".equalsIgnoreCase(method)) {
            if (BaseUtils.isBlank(key))
                return "key is required in DAO method!";
            
            rs = GenDbSource.genDAO(poolName,
                    tbName, BaseUtils.isBlank(sql) ? null : sql,
                    key, packageName == null ? "" : packageName);
        }

        if (BaseUtils.isBlank(packageName))
            rs = rs.replace("package ;", "");
        return rs;
    }

    public String getEmptyIfInputBlank(String input) {
        return getEmptyIfInputBlank(input, "");
    }

    public String getEmptyIfInputBlank(String input, String output) {
        return BaseUtils.isBlank(input) ? output : input;
    }


%>
<html>
<head>
    <title>Gen Bean</title>
    <meta charset="utf-8">
    <style>
        input {
            /*width: 100%;*/
        }

        label {
            cursor: pointer;
        }

        #txtSQL {
            width: 100%;
            height: 100px;
        }

        #txtResult {
            width: 100%;
            height: 500px;
        }
    </style>
    <script>
        function copy() {
            var copyTextarea = document.querySelector('#txtResult');
            copyTextarea.select();
            try {
                var successful = document.execCommand('copy');
                var msg = successful ? 'successful' : 'unsuccessful';
                console.log('Copying text command was ' + msg);
            } catch (err) {
                console.log('Oops, unable to copy');
            }
        }
    </script>
</head>
<body style="margin-left: 16px;">
<form action="" method="post" id="formGenBean">
    <div style="margin-top: 20px;margin-right: 50px; margin-bottom: 16px">
        <table border="0" width="100%">
            <tr>
                <td width="10%">Method</td>
                <td width="10%" colspan="1">
                    <input id="dto_checked" type="radio" name="method" value="DTO" checked>
                    <label for="dto_checked">DTO</label>
                    <input id="dao_checked" type="radio" name="method"
                           value="DAO"<%= "DAO".equalsIgnoreCase(request.getParameter("method"))?"checked=\"true\"":""%>>
                    <label for="dao_checked">DAO</label>
                </td>
                <td rowspan="5" width="65%">SQL<br>
                    <textarea form="formGenBean" name="sql"
                              id="txtSQL"><%=sql%></textarea>
                </td>
            </tr>
            <tr>
                <td>Pool</td>
                <td>
                    <input name="pool"  
                           value="<%=poolName%>"/>
                </td>
            </tr>
            <tr>
                <td>Table Name</td>
                <td><input name="table" autofocus="true" required
                           value="<%=tbName%>"/>
                </td>
            </tr>
            <tr>
                <td>Key</td>
                <td><input name="key"
                           value="<%=key%>"/>
            </tr>
            <tr>
                <td>Package name</td>
                <td><input name="package"
                           value="<%=packageName%>"/>
            </tr>
            <tr>
                <td colspan="2" style="text-align: left; padding-top: 15px">
                    <button type='submit' name='actionBtn'>Submit</button>
                </td>
            </tr>

        </table>
        <br>
        <textarea id="txtResult"><%=rs%></textarea>
        <br>
        <br>
        <button type="button" id="btCopy" onclick="copy()">Copy</button>
    </div>
</form>