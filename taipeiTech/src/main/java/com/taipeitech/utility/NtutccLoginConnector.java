package com.taipeitech.utility;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class NtutccLoginConnector {

    public static String login_1(String account, String password)
            throws Exception {
        String uri = "https://captiveportal-login.ntut.edu.tw/auth/index.html/u";
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("user", account);
            params.put("password", password);
            params.put("Login", "登入(Login)");
            return Connector.getDataByPost(uri, params, "big5");
        } catch (Exception e) {
            throw new Exception("Ntutcc登入時發生錯誤");
        }
    }

    public static String login_2_1(String uri, String account, String password)
            throws Exception {
        try {
            String result = Connector.getDataByGet(uri, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] nodes = tagNode.getElementsByName("A", true);
            String redirectUri = nodes[0].getAttributeByName("HREF");
            redirectUri = redirectUri.replace("amp;", "");
            result = login_2_2(redirectUri, account, password);
            return result;
        } catch (Exception e) {
            throw new Exception("Ntutcc登入時發生錯誤");
        }
    }

    public static String login_2_2(String redirectUri, String account,
                                   String password) throws Exception {
        try {
            String result = Connector.getDataByGet(redirectUri, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] nodes = tagNode.getElementsByName("input", true);
            String __VIEWSTATE = nodes[0].getAttributeByName("value");
            String __EVENTVALIDATION = nodes[1].getAttributeByName("value");

            HashMap<String, String> params = new HashMap<>();
            params.put("__VIEWSTATE", __VIEWSTATE);
            params.put("__EVENTVALIDATION",
                    __EVENTVALIDATION);
            params.put("__EVENTTARGET", "");
            params.put("__EVENTARGUMENT", "");
            params.put("TxtBox_loginName", account);
            params.put("TxtBox_password", password);
            params.put("btnSubmit_AD", "登入");
            result = Connector.getDataByPost(redirectUri, params, "big5");
            return result;
        } catch (Exception e) {
            throw new Exception("Ntutcc登入時發生錯誤");
        }
    }

    public static String getRedirectUri(String uri) throws Exception {
        try {
            URLConnection con = new URL(uri).openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            String redirectUri = con.getURL().toString();
            is.close();
            return redirectUri;
        } catch (Exception e) {
            throw new Exception("Ntutcc登入時發生錯誤");
        }
    }
}
