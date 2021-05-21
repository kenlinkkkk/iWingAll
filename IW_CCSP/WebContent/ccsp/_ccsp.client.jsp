<%@page import="com.xxx.aps.processor.SaveActionHis" %>
<%@page import="com.ligerdev.appbase.utils.textbase.StringToolUtils" %>
<%@page import="org.apache.http.impl.client.CloseableHttpClient" %>
<%@page import="com.ligerdev.appbase.utils.textbase.Log4jLoader" %>
<%@page import="org.apache.http.entity.ByteArrayEntity" %>
<%@page import="com.xxx.aps.XmlConfigs" %>
<%@page import="org.apache.http.client.methods.CloseableHttpResponse" %>
<%@page import="org.apache.http.client.methods.HttpPost" %>
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
    private static CloseableHttpClient httpclient;

    static {
        try {
            httpclient = com.nip.net.http.HttpUtils.getInstance().getClient(30000);
            // Log4jLoader.getLogger().info("======> create http client");
        } catch (Exception e) {
            Log4jLoader.getLogger().info("Exception: " + e.getMessage());
        }
    }

    public static int insertMT(String transid, MtHis mt) {
        XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");

        if (XmlConfigs.short_sms_table) {
            String table = "mt_" + new SimpleDateFormat("yyyy").format(new Date());
            return xbaseDAO.insertBean(transid, table, mt);
        }
        return xbaseDAO.insertBean(transid, mt);
    }

    public static int insertMO(String transid, MoHis mo) {
        XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");

        if (XmlConfigs.short_sms_table) {
            String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
            return xbaseDAO.insertBean(transid, table, mo);
        }
        return xbaseDAO.insertBean(transid, mo);
    }

    public static final int sendSMS1(String transid, MtHis mt) {
        // save DB và reurn int
        String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), mt.getShortcode());
        int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
        mt.setResult(rs);
        insertMT(transid, mt);
        return rs;
    }

    public static final int sendSMS1Brandname(String transid, MtHis mt, String shortcode) {
        // save DB và reurn int
        String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), shortcode);
        int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
        mt.setResult(rs);
        insertMT(transid, mt);
        return rs;
    }

    public static final String unRegMsisdn(String transid, String msisdn,String shortcode ,String command, String pkgCode,String desc) {
        String channel = "CP";
        msisdn = BaseUtils.formatMsisdn(msisdn, "84", "");

        String xml =
                "<?xml version='1.0' encoding='UTF-8'?>" +
                        "<soapenv:Envelope xmlns:obj='http://object.app.telsoft/' xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<obj:receiverServiceReq>" +
                        "<ISDN>" + msisdn + "</ISDN>" +
                        "<ServiceCode>" + shortcode + "</ServiceCode>" +
                        "<CommandCode>" + command + "</CommandCode>" +
                        "<PackageCode>" + pkgCode + "</PackageCode>" +
                        "<SourceCode>" + channel + "</SourceCode>" +
                        "<RequestId>"+new SimpleDateFormat("MMddHHmmss").format(new Date())+"</RequestId>" +
                        "<User>" + XmlConfigs.username + "</User>" +
                        "<Password>" + XmlConfigs.password + "</Password>" +
                        "<Description>" + desc + "</Description>" +
                        "</obj:receiverServiceReq>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(XmlConfigs.url_action_wcc);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("Connection", "close");
            post.addHeader("SOAPAction", "");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);
            logger.info(transid + ", actionWCC request: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            resp = httpclient.execute(post);
            String response = BaseUtils.readInputStream(resp.getEntity().getContent());
            logger.info(transid + ", actionWCC response: " + String.valueOf(response).replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            response = parseReturnCode(response);
            logger.info(transid + ", actionWCC(" + msisdn + "," + command + ") -> CCSP response: " + response);
            return response;

        } catch (Exception e) {
            logger.error(transid + ", actionWCC(" + msisdn + "," + command + ") -> CCSP error", e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            post.releaseConnection();
        }
        return "500";
    }

    public static final void sendSMS1_delay(final String transid, final MtHis mt, final int seconds) {
        // save DB và reurn int
        new Thread() {
            public void run() {
                BaseUtils.sleep(seconds);

                String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), mt.getShortcode());
                int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
                mt.setResult(rs);
                insertMT(transid, mt);
            }
        }.start();
    }

    public static final String sendSMS2(String transid, MtHis mt) {
        // save db và return nguyên bản String từ CCSP
        String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), mt.getShortcode());
        int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
        mt.setResult(rs);
        insertMT(transid, mt);
        return temp;
    }

    public static final String sendSMS0(String transid, String msisdn, String content) {
        return sendSMS0(transid, msisdn, content, null);
    }

    public static final String sendSMS0(String transid, String msisdn, String content, String shortcode) {
        long l1 = System.currentTimeMillis();
        String xml =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:obj=\"http://object.app.telsoft/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <obj:sendMessage>\n" +
                        "         <ServiceCode>" + XmlConfigs.shortcode + "</ServiceCode>\n" +
                        "         <ISDN>" + msisdn + "</ISDN>\n" +
                        "         <Content>" + content + "</Content>\n" +
                        "         <UseBrandname>" + ("brandname".equalsIgnoreCase(shortcode) ? "1" : "0") + "</UseBrandname>\n" +
                        "         <User>" + XmlConfigs.username + "</User>\n" +
                        "         <Password>" + XmlConfigs.password + "</Password>\n" +
                        "      </obj:sendMessage>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";

        Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(XmlConfigs.url_send_mt);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("Connection", "close");
            post.addHeader("SOAPAction", "");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);
            logger.info(transid + ", xmlSendMT: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));
            long l2 = System.currentTimeMillis();

            resp = httpclient.execute(post);
            String str = BaseUtils.readInputStream(resp.getEntity().getContent()) + "";
            long l3 = System.currentTimeMillis();

            logger.info(transid + ", xmlRespMT: " + str.replace("\n", " ").replace("\t", " ").replaceAll(" +", " ")
                    + ", time1: " + BaseUtils.getDurations(l2, l1)
                    + ", time2: " + BaseUtils.getDurations(l3, l2)
            );
            return str;

        } catch (Exception e) {
            logger.error(transid + ", Send SMS (" + msisdn + ", " + StringToolUtils.unicode2ASII(content) + ") -> CCSP error", e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            post.releaseConnection();
        }
        return null;
    }

    public static final String updateSmsRemind(String transid, String service_code, String package_code, String content) {
        String xml =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:obj=\"http://object.app.telsoft/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <obj:exeReceivedCP_MT>\n" +
                        "         <ServiceCode>" + XmlConfigs.shortcode + "</ServiceCode>\n" +
                        "         <PackageCode>" + package_code + "</PackageCode>\n" +
                        "         <Contents>" + content + "</Contents>\n" +
                        "         <UserName>" + XmlConfigs.username + "</UserName>\n" +
                        "         <Password>" + XmlConfigs.password + "</Password>\n" +
                        "      </obj:exeReceivedCP_MT>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";

        Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(XmlConfigs.url_mt_remind);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("Connection", "close");
            post.addHeader("SOAPAction", "");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);
            logger.info(transid + ", updateSmsRemind request: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            resp = httpclient.execute(post);
            String response = BaseUtils.readInputStream(resp.getEntity().getContent());
            logger.info(transid + ", updateSmsRemind response: " + String.valueOf(response).replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            response = parseReturnCode(response);
            logger.info(transid + ", update sms remind service_code: " + service_code + " package_code: " +
                    package_code + " content: " + content + " -> CCSP response: " + response);
            return response;

        } catch (Exception e) {
            logger.error(transid + ", update sms remind service_code: " + service_code + " package_code: "
                    + package_code + " content: " + content + " -> CCSP Error", e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            post.releaseConnection();
        }
        return "500";
    }


    public static String actionWCC(String transid, String msisdn, String command, String pkgCode, String desc) {
        String channel = "CP";
        msisdn = BaseUtils.formatMsisdn(msisdn, "84", "");

        String xml =
                "<?xml version='1.0' encoding='UTF-8'?>" +
                        "<soapenv:Envelope xmlns:obj='http://object.app.telsoft/' xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<obj:receiverServiceReq>" +
                        "<ISDN>" + msisdn + "</ISDN>" +
                        "<ServiceCode>" + XmlConfigs.shortcode + "</ServiceCode>" +
                        "<CommandCode>" + command + "</CommandCode>" +
                        "<PackageCode>" + pkgCode + "</PackageCode>" +
                        "<SourceCode>" + channel + "</SourceCode>" +
                        "<User>" + XmlConfigs.username + "</User>" +
                        "<Password>" + XmlConfigs.password + "</Password>" +
                        "<Description>" + desc + "</Description>" +
                        "</obj:receiverServiceReq>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(XmlConfigs.url_action_wcc);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("Connection", "close");
            post.addHeader("SOAPAction", "");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);
            logger.info(transid + ", actionWCC request: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            resp = httpclient.execute(post);
            String response = BaseUtils.readInputStream(resp.getEntity().getContent());
            logger.info(transid + ", actionWCC response: " + String.valueOf(response).replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            response = parseReturnCode(response);
            logger.info(transid + ", actionWCC(" + msisdn + "," + command + ") -> CCSP response: " + response);
            return response;

        } catch (Exception e) {
            logger.error(transid + ", actionWCC(" + msisdn + "," + command + ") -> CCSP error", e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            post.releaseConnection();
        }
        return "500";
    }


    public static int chargeContent(String transid, String msisdn, int fee, String cateid,
                                    String contentid, String pkgcode, String requestid, String mocontent) {
        // /mbfn/sb/SOAPRequestServicecps/receiverPackageReq
        String spid_ccsp = "001";
        String cpid_ccsp = "001";

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<soapenv:Envelope xmlns:obj='http://object.app.telsoft/' xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<obj:minusMoneyCheckMO>" +
                "<ServiceCode>" + XmlConfigs.shortcode + "</ServiceCode>" +
                "<ISDN>" + msisdn + "</ISDN>" +
                "<RequestId>" + requestid + "</RequestId>" +
                "<PackageCode>" + pkgcode + "</PackageCode>" +
                "<PackageName>" + pkgcode + "</PackageName>" +
                "<SP_ID>" + spid_ccsp + "</SP_ID>" +
                "<CP_ID>" + cpid_ccsp + "</CP_ID>" +
                "<Content_ID>" + contentid + "</Content_ID>" +
                "<Category_ID>" + cateid + "</Category_ID>" +
                "<Amount>" + fee + "</Amount>" +
                "<UserName>" + XmlConfigs.username + "</UserName>" +
                "<Password>" + XmlConfigs.password + "</Password>" +
                "</obj:minusMoneyCheckMO>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(XmlConfigs.url_charge_content);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("Connection", "close");
            post.addHeader("SOAPAction", "");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);

            logger.info(transid + ", chargeContent request: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));
            resp = httpclient.execute(post);
            String response = BaseUtils.readInputStream(resp.getEntity().getContent());
            logger.info(transid + ", chargeContent response: " + String.valueOf(response).replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            response = parseReturnCode(response);
            logger.info(transid + ", chargeContent(" + msisdn + "," + fee + "," + requestid + ") -> CCSP response: " + response);

            if ("OK".equalsIgnoreCase(response) || "1".equalsIgnoreCase(response)) { // return 1
                String note = null;
                String cpid = mocontent;
                String channel = "SMS";
                Date expireTime = null;
                int regdate = 0;
                int expireint = 0;
                int activeint = 0;
                String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(new Date());

                ActionHis a = new ActionHis(0, msisdn, "CONTENT", new Date(), fee, transid, note, channel, 0, pkgcode,
                        cpid, expireTime, regdate,
                        null, null, null, null, null,
                        0, 0,
                        hisnote1, null, null);

                SaveActionHis.queue.put(a);
                logger.info(transid + ", chargeContent rs: OK");
                return XmlConfigs.CHARGE_OK;
            }
            if ("lowbalance".equalsIgnoreCase(response) || "4012".equalsIgnoreCase(response)) { // return 4012
                logger.info(transid + ", chargeContent rs: LOWBALANCE");
                return XmlConfigs.CHARGE_LOWBALANCE;
            }
            logger.info(transid + ", chargeContent rs: ERROR");
            return BaseUtils.parseInt(response, 2);

        } catch (Exception e) {
            logger.error(transid + ", chargeContent(" + msisdn + "," + fee + "," + requestid + ") -> CCSP error", e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            post.releaseConnection();
        }
        return XmlConfigs.CHARGE_ERROR;
    }

    public static final String forward2ccsp(String transid, String xml, String url) {
        Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("Connection", "close");
            post.addHeader("SOAPAction", "");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);

            logger.info(transid + ", forward2ccspREQ: url: " + url + ", xml: "
                    + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));

            resp = httpclient.execute(post);
            String str = BaseUtils.readInputStream(resp.getEntity().getContent()) + "";

            logger.info(transid + ", forward2ccspRESP: "
                    + String.valueOf(str).replace("\n", " ").replace("\t", " ").replaceAll(" +", " "));
            return str;

        } catch (Exception e) {
            logger.error(transid + ", Exception : " + e.getMessage(), e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            post.releaseConnection();
        }
        return null;
    }

    private static String parseReturnCode(String input) {
        if (input == null) {
            return null;
        }
        String startTag = "<return>";
        String endTag = "</return>";
        int start = input.indexOf(startTag);
        int end = input.indexOf(endTag);
        if (start < 0 || end < 0) {
            return null;
        }
        String result = input.substring(start + startTag.length(), end);
        return result;
    }
%>
