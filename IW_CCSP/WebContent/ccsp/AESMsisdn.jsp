<%@page import="com.ligerdev.appbase.utils.entities.PairStringString" %>
<%@page import="java.util.Map.Entry" %>
<%-- <%@page import="com.xxx.aps.XmlConfigs"%> --%>
<%@page import="com.ligerdev.appbase.utils.db.XBaseDAO" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils" %>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpServerUtils" %>
<%@page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%-- <%@include file="_utils.app.jsp"%>    --%>
<%-- <%@include file="_ccsp.client.jsp"%>    --%>

<%
    if (request.getParameter("compile") != null) {
        out.print("compile ok, v1.0");
        return;
    }

//    curl 'http://localhost:8888/ccsp/AESMsisdn.jsp?compile'

    String msisdn = request.getParameter("msisdn");

    logger.info("@@@@@@@@@decrypt : " + msisdn);
    msisdn = decrypt(msisdn, "Vny5fgACqu6lGsbq");

    logger.info("@@@@@@@@@msisdn : " + msisdn);

    out.print(msisdn);

%>


<%!
    private static BaseDAO baseDAO = BaseDAO.getInstance("main");
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static Logger logger = Logger.getLogger("LOG");

    public static String decrypt(String encryptedText, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] cipherText = Base64.decodeBase64(encryptedText.getBytes("UTF8"));
            String decryptedString = new String(cipher.doFinal(cipherText), "UTF-8");
            logger.info("decrypt:" + encryptedText + " resutl:" + decryptedString);
            return decryptedString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

%>