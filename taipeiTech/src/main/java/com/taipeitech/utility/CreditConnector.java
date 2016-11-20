package com.taipeitech.utility;

import android.os.Handler;

import com.taipeitech.model.CreditInfo;
import com.taipeitech.model.GeneralCredit;
import com.taipeitech.model.GeneralCreditInfo;
import com.taipeitech.model.SemesterCredit;
import com.taipeitech.model.StudentCredit;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreditConnector {
    private final static String POST_CREDIT_URI = "http://nportal.ntut.edu.tw/ssoIndex.do?apOu=aa_003&apUrl=http://aps-stu.ntut.edu.tw/StuQuery/LoginSID.jsp";
    private final static String CREDITS_URI = "http://aps-stu.ntut.edu.tw/StuQuery/LoginSID.jsp";
    private final static String CREDIT_URI = "http://aps-stu.ntut.edu.tw/StuQuery/QryScore.jsp";
    private final static String GENERAL_URI = "http://aps-stu.ntut.edu.tw/StuQuery/QryLAECourse.jsp";
    private final static String STANDARD_URI = "http://aps.ntut.edu.tw/course/tw/Cprog.jsp";
    private final static String CURRENT_URI = "http://aps-stu.ntut.edu.tw/StuQuery/QrySCWarn.jsp";

    public static ArrayList<String> matrics = new ArrayList<>();
    public static Boolean isHaveError = false;

    public static String loginCredit() throws Exception {
        try {
            String result = Connector.getDataByGet(POST_CREDIT_URI, "big5", "http://nportal.ntut.edu.tw/aptreeList.do?apDn=ou=aa,ou=aproot,o=ldaproot");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] nodes = tagNode.getElementsByAttValue("name",
                    "sessionId", true, false);
            String sessionId = nodes[0].getAttributeByName("value");
            nodes = tagNode
                    .getElementsByAttValue("name", "userid", true, false);
            String userid = nodes[0].getAttributeByName("value");
            HashMap<String, String> params = new HashMap<>();
            params.put("sessionId", sessionId);
            params.put("userid", userid);
            result = Connector.getDataByPost(CREDITS_URI, params, "big5");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("登入學生查詢系統時發生錯誤");
        }
    }

    public static StudentCredit getCredits(Handler progressHandler)
            throws Exception {
        try {
            isHaveError = false;
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-2");
            String result = Connector.getDataByPost(CREDIT_URI, params, "big5");
            int total = getCourseCount(result);
            progressHandler.obtainMessage(1, total).sendToTarget();
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] nodes = tagNode.getElementsByAttValue("border", "1",
                    true, false);

            TagNode[] titles = tagNode.getElementsByName("H3", true);
            int t = titles.length - nodes.length;
            int count = 0;
            StudentCredit studentCredit = new StudentCredit();
            studentCredit = getCurrentCredit(studentCredit);
            for (TagNode table : nodes) {
                String[] temp = titles[t].getText().toString().split(" ");
                TagNode[] rows = table.getElementsByName("tr", true);
                SemesterCredit semester = new SemesterCredit();
                semester.setYear(temp[0]);
                semester.setSemester(temp[3]);
                for (int i = 1; i < rows.length - 6; i++) {
                    TagNode[] cols = rows[i].getElementsByName("th", true);
                    CreditInfo credit = new CreditInfo();
                    credit.setCourseNo(cols[0].getText().toString());
                    credit.setCourseName(cols[2].getText().toString());
                    credit.setCredit((int) Double.parseDouble(cols[5].getText()
                            .toString()));
                    credit.setScore(cols[6].getText().toString());
                    int type = getCourseType(cols[0].getText().toString(),
                            cols[6].getText().toString());
                    credit.setType(type);
                    semester.addCreditInfo(credit);
                    count++;
                    progressHandler.obtainMessage(0, count).sendToTarget();
                }
                TagNode[] cols = rows[rows.length - 3].getElementsByName("td",
                        true);
                semester.setConductScore(cols[0].getText().toString());
                cols = rows[rows.length - 4].getElementsByName("td", true);
                semester.setScore(cols[0].getText().toString());
                studentCredit.addSemesterCredit(semester);
                t++;
            }
            studentCredit = getGeneralCredit(studentCredit);
            studentCredit = Utility.cleanString(studentCredit);
            return studentCredit;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("學分資料讀取時發生錯誤");
        }
    }

    private static StudentCredit getGeneralCredit(StudentCredit studentCredit) {
        try {
            String result = Connector.getDataByGet(GENERAL_URI, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] rows = tagNode.getElementsByName("tr", true);
            GeneralCredit general = null;
            for (int i = 2; i < rows.length; i++) {
                TagNode[] cols = rows[i].getElementsByName("td", true);
                if (cols.length == 10) {
                    general = new GeneralCredit();
                    general.setTypeName(cols[0].getText().toString());
                    String mustCoreCredit = cols[1].getText().toString();
                    mustCoreCredit = Utility.cleanString(mustCoreCredit);
                    if (mustCoreCredit.length() > 0) {
                        general.setMustCoreCredit((int) Double
                                .parseDouble(mustCoreCredit));
                    }
                    studentCredit.addGeneralCredit(general);
                }
                String sem = cols[cols.length - 6].getText().toString();
                sem = Utility.cleanString(sem);
                if (sem.length() > 0) {
                    GeneralCreditInfo course = new GeneralCreditInfo();
                    course.setYear(sem.split("-")[0]);
                    course.setSem(sem.split("-")[1]);
                    if (cols[cols.length - 5].getText().toString()
                            .contains("＊")) {
                        course.setCore(true);
                    }
                    course.setCourseName(cols[cols.length - 3].getText()
                            .toString());
                    course.setCredit((int) Double
                            .parseDouble(cols[cols.length - 2].getText()
                                    .toString()));
                    general.addGeneral(course);
                }
            }
        } catch (Exception ex) {
            isHaveError = true;
            studentCredit.getGeneralCredits().clear();
        }
        return studentCredit;
    }

    private static StudentCredit getCurrentCredit(StudentCredit studentCredit) {
        try {
            String result = Connector.getDataByGet(CURRENT_URI, "big5");
            if (result.contains("查無本學期選課資料")) {
                return studentCredit;
            }
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] titles = tagNode.getElementsByName("H3", true);
            SemesterCredit semester = new SemesterCredit();
            String[] temp = titles[0].getText().toString().split("學年度 第 ");
            semester.setYear(temp[0]);
            semester.setSemester(temp[1].substring(0, 1));
            TagNode[] rows = tagNode.getElementsByName("tr", true);
            for (int i = 1; i < rows.length; i++) {
                TagNode[] cols = rows[i].getElementsByName("td", true);
                CreditInfo credit = new CreditInfo();
                credit.setCourseNo(cols[0].getText().toString());
                credit.setCourseName(cols[2].getText().toString());
                cols = rows[i].getElementsByName("th", true);
                credit.setCredit((int) Double.parseDouble(cols[0].getText()
                        .toString()));
                credit.setScore("XD");
                credit.setType(0);
                semester.addCreditInfo(credit);
            }
            studentCredit.addSemesterCredit(semester);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentCredit;
    }

    private static int getCourseCount(String result) {
        TagNode tagNode;
        tagNode = new HtmlCleaner().clean(result);
        TagNode[] nodes = tagNode.getElementsByAttValue("border", "1", true,
                false);
        int count = 0;
        for (TagNode table : nodes) {
            TagNode[] rows = table.getElementsByName("tr", true);
            for (int i = 1; i < rows.length - 6; i++) {
                count++;
            }
        }
        return count;

    }

    private static int getCourseType(String courseNo, String score) {
        try {
            score = Utility.cleanString(score);
            courseNo = Utility.cleanString(courseNo);
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(score);
            if (m.matches()) {
                if (Integer.parseInt(score) < 60) {
                    return 0;
                }
            } else {
                return 0;
            }

            String type = CourseConnector.getCourseType(courseNo);
            if (type.contains("○")) {
                return 1;
            } else if (type.contains("△")) {
                return 2;
            } else if (type.contains("☆")) {
                return 3;
            } else if (type.contains("●")) {
                return 4;
            } else if (type.contains("▲")) {
                return 5;
            } else if (type.contains("★")) {
                return 6;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            isHaveError = true;
            return 0;
        }
    }

    public static ArrayList<String> getYearList() throws Exception {
        try {
            ArrayList<String> year_list = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-1");
            String result = Connector
                    .getDataByPost(STANDARD_URI, params, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] rows = tagNode.getElementsByName("a", true);
            for (TagNode row : rows) {
                String year = row.getText().toString();
                year_list.add(year);
            }
            return year_list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("入學年度清單讀取時發生錯誤");
        }
    }

    public static ArrayList<String> getDivisionList(String year)
            throws Exception {
        try {
            matrics.clear();
            ArrayList<String> divison_list = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-2");
            params.put("year", year);
            String result = Connector
                    .getDataByPost(STANDARD_URI, params, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] rows = tagNode.getElementsByName("a", true);
            for (TagNode row : rows) {
                String division = row.getText().toString();
                String[] temp = row.getAttributeByName("href").split("=");
                String matric = temp[temp.length - 1];
                matrics.add(matric);
                divison_list.add(division);
            }
            return divison_list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("學制清單讀取時發生錯誤");
        }
    }

    public static ArrayList<String> getDepartmentList(String year, int index)
            throws Exception {
        try {
            ArrayList<String> departemt_list = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-3");
            params.put("year", year);
            params.put("matric", matrics.get(index));
            String result = Connector
                    .getDataByPost(STANDARD_URI, params, "big5");
            TagNode tagNode;
            tagNode = new HtmlCleaner().clean(result);
            TagNode[] tables = tagNode.getElementsByAttValue("border", "1",
                    true, false);
            TagNode[] rows = tables[0].getElementsByName("a", true);
            for (TagNode row : rows) {
                String department = row.getText().toString();
                departemt_list.add(department);
            }
            return departemt_list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("系所清單讀取時發生錯誤");
        }
    }

    public static ArrayList<String> getStandardCredit(String year, int index,
                                                      String department) throws Exception {
        try {
            ArrayList<String> standard = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            params.put("format", "-3");
            params.put("year", year);
            params.put("matric", matrics.get(index));
            String result = Connector
                    .getDataByPost(STANDARD_URI, params, "big5");
            result = result.replace("<td", "</td><td");
            result = result.replace("<tr>", "</td><tr>");
            HtmlCleaner cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setUseCdataForScriptAndStyle(true);
            props.setRecognizeUnicodeChars(true);
            props.setUseEmptyElementTags(true);
            props.setAdvancedXmlEscape(true);
            props.setTranslateSpecialEntities(true);
            props.setBooleanAttributeValues("empty");
            result = new PrettyHtmlSerializer(props).getAsString(result);
            TagNode tagNode = cleaner.clean(result);
            TagNode[] tables = tagNode.getElementsByAttValue("border", "1",
                    true, false);
            TagNode[] rows = tables[0].getElementsByName("tr", true);
            for (int i = 1; i < rows.length; i++) {
                TagNode[] cols = rows[i].getElementsByName("td", true);
                String temp = cols[0].getText().toString();
                if (temp.contains(department)) {
                    for (int j = 1; j < 9; j++) {
                        String credit = Utility.cleanString(cols[j].getText()
                                .toString());
                        standard.add(credit);
                    }
                    return standard;
                }
            }
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("畢業學分標準讀取時發生錯誤");
        }
    }
}
