<%@page import="com.ligerdev.appbase.utils.entities.PairStringString" %>
<%@page import="java.util.Map.Entry" %>
<%@page import="com.xxx.aps.XmlConfigs" %>
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
<%@ page import="com.xxx.aps.logic.db.orm.*" %>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>

<%!
    private static BaseDAO baseDAO = BaseDAO.getInstance("main");
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static Logger logger = Logger.getLogger("LOG");
    private static CacheSyncFile cache = CacheSyncFile.getInstance(500000);

    public static Subscriber getSubscriber(String transid, String msisdn, String pkgCode) {
        String sql = "select * from subscriber where msisdn = '" + msisdn + "' and package_id = '" + pkgCode + "'";
        return xbaseDAO.getBeanBySql(transid, Subscriber.class, sql);
    }

    public static boolean isTingTingService() {
        return XmlConfigs.servicename.toLowerCase().contains("tingting");
    }


    public static boolean isHocVuiService() {
        return XmlConfigs.servicename.toLowerCase().contains("hocvui");
    }

    public static boolean isPokiService() {
        return XmlConfigs.servicename.toLowerCase().contains("poki");
    }

    public static String getHeaderString(String transid, HttpServletRequest req) {
        Enumeration<String> enums = req.getHeaderNames();
        String s = "";

        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            Enumeration<String> valuesEnums = req.getHeaders(key);

            while (valuesEnums.hasMoreElements()) {
                String value = valuesEnums.nextElement();
                s += key + " = " + value + ", ";
            }
        }
        return s;
    }

    public static String getParameterString(String transid, HttpServletRequest req) {
        Map<String, String[]> map = req.getParameterMap();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> iter = set.iterator();
        String s = "";

        while (iter.hasNext()) {
            Entry<String, String[]> e = (Entry<String, String[]>) iter.next();
            String key = e.getKey();
            String value[] = e.getValue();

            for (int i = 0; value != null && i < value.length; i++) {
                s += key + " = " + value[i] + ", ";
            }
        }
        return s;
    }

    public static ArrayList<PairStringString> getHeaders(String transid, HttpServletRequest req, boolean log) {
        ArrayList<PairStringString> headers = new ArrayList<PairStringString>();
        Enumeration<String> enums = req.getHeaderNames();

        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            Enumeration<String> valuesEnums = req.getHeaders(key);

            while (valuesEnums.hasMoreElements()) {
                String value = valuesEnums.nextElement();
                if (log) {
                    logger.info(transid + ", Header: key = " + key + ", value = " + value);
                }
                headers.add(new PairStringString(key, value));
            }
        }
        return headers;
    }

    public static ArrayList<PairStringString> getParameters(String transid, HttpServletRequest req, boolean log) {
        Map<String, String[]> map = req.getParameterMap();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> iter = set.iterator();
        ArrayList<PairStringString> params = new ArrayList<PairStringString>();

        while (iter.hasNext()) {
            Entry<String, String[]> e = (Entry<String, String[]>) iter.next();
            String key = e.getKey();
            String value[] = e.getValue();

            for (int i = 0; value != null && i < value.length; i++) {
                if (log) {
                    logger.info(transid + ", Parameter: key = " + key + ", value = " + value[i]);
                }
                params.add(new PairStringString(key, value[i]));
            }
        }
        return params;
    }

    public static String postParams(String transid, String requestURL, HashMap<String, String> postDataParams, int timeout) {
        URL url;
        String response = "";
        try {
            if (requestURL.contains("\\?")) {
                requestURL += "?";
            }
            String queryStr = buildQueryStr(postDataParams);
            // requestURL += queryStr;

            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(queryStr);

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "(httpcode: " + responseCode + ")";
            }
        } catch (Exception e) {
            logger.info(transid + ", Exception: " + e.getMessage(), e);
        }
        return response;
    }

    public static String get(String transid, String requestURL, HashMap<String, String> postDataParams, int timeout) {
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            if (requestURL.contains("\\?")) {
                requestURL += "?";
            }
            String queryStr = buildQueryStr(postDataParams);
            requestURL += queryStr;

            URL url = new URL(requestURL);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            httpConnection.setConnectTimeout(timeout);
            httpConnection.setReadTimeout(timeout);

            is = httpConnection.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = null;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line + "\n");
            }
            return result.toString().trim();

        } catch (Exception e) {
            logger.info(transid + ", url = " + requestURL + ", Exception: " + e.getMessage(), e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
            try {
                isr.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (Exception e) {
            }
            try {
                httpConnection.disconnect();
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static String buildQueryStr(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public static String getFirstIPForwardedFor(HttpServletRequest request) {
        String useripTemp = request.getRemoteHost();
        if (request.getHeader("X-Forwarded-For") != null) {
            useripTemp = request.getHeader("X-Forwarded-For");
            if (useripTemp.contains(",")) {
                useripTemp = useripTemp.split(",")[0].trim();
            }
        }
        return useripTemp;
    }
%>