package com.alexey.samsung;


import org.apache.logging.log4j.core.script.ScriptRef;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by aokly on 25.09.2016.
 */
public class WebSelenium implements AutoCloseable {

    static WebDriver driver;
    ChromeOptions option;
    static String proxy = "";
    DesiredCapabilities capabilities;

    public void fillAddUserPage(String login, String password, String name, String mail) throws InterruptedException {
        //Thread.sleep(1000);

        driver.get("http://mdl.sch239.net/user/editadvanced.php?id=-1");
        WebElement loginW = driver.findElement(By.name("username"));
        loginW.sendKeys(login.toLowerCase());
        WebElement passwordW = driver.findElement(By.name("newpassword"));
        passwordW.sendKeys(password);
        String s[] = name.split(" ");
        WebElement nameW = driver.findElement(By.name("firstname"));
        nameW.sendKeys(s[0]);
        WebElement lastNameW = driver.findElement(By.name("lastname"));
        lastNameW.sendKeys(s[1]);
        WebElement mailW = driver.findElement(By.name("email"));
        mailW.sendKeys(mail);
        WebElement btn = driver.findElement(By.name("submitbutton"));
        btn.click();
        Thread.sleep(4000);
    }

    public void fillAddUserPage(ArrayList<String> lst) throws InterruptedException {
        fillAddUserPage(lst.get(0), lst.get(1), lst.get(2), lst.get(3));
    }

    public void loginToMdl() {
        driver.get("http://mdl.sch239.net/");
        WebElement login = driver.findElement(By.name("username"));
        login.sendKeys("kluninao");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("kluninao");
        WebElement btn = driver.findElement(By.id("loginbtn"));
        btn.click();
    }


    WebSelenium(String proxyUrl) {
        System.out.println("конструктор selenium");
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(proxyUrl)
                .setFtpProxy(proxyUrl)
                .setSslProxy(proxyUrl);
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, proxy);

        if (System.getProperty("os.name").contains("inux")) {
            System.setProperty("webdriver.chrome.driver", "chromedriver");
        } else {
            System.setProperty("webdriver.chrome.driver", "C://Program Files (x86)//chromedriver.exe");
        }
        driver = new ChromeDriver();

        //driver.manage().window().
    }

    public WebSelenium() {
        capabilities = DesiredCapabilities.chrome();
        if (System.getProperty("os.name").contains("inux")) {
            System.setProperty("webdriver.chrome.driver", "chromedriver");
        } else {
            System.setProperty("webdriver.chrome.driver", "C://Program Files (x86)//chromedriver.exe");
        }
        driver = new ChromeDriver(capabilities);
    }

    public String getIpFromPage() {
        driver.get("https://2ip.ru/");
        WebElement ipBlock = driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div/div[3]"));
        return ipBlock.getText().split("\n")[1];
    }

    static String loadCurPageHTTP(String urlS) throws IOException {
        //driver.get(url);
        System.setProperty("http.proxyHost", "http://pr0xii.com/");
        System.setProperty("http.proxyPort", "8080");
        URL url = new URL(urlS);
        InputStream in = url.openStream();
        String result = "";
        try {
            System.out.println("asd");
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            String string = reader.readLine();
            while (string != null) {
                result += string + "\n";
                string = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
// Now, let's 'unset' the proxy.
        System.setProperty("http.proxyHost", "");
        return result;
    }

    public void renameTests(String name, String value) throws InterruptedException {
        driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(value);
        //*[@id="single_select57f3c955e54454"]

        //driver.get(path);
        WebElement table = driver.findElement(By.id("categoryquestions"));
        List<WebElement> ql = table.findElements(By.tagName("tr"));
        ArrayList<String> qLinks = new ArrayList<>();
        Thread.sleep(100);
        for (WebElement q : ql) {
            List<WebElement> tdList = q.findElements(By.tagName("td"));
            if (tdList.size() != 0) {
                ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
                qLinks.add(tds.get(2).findElement(By.tagName("a")).getAttribute("href"));
            }
        }
        Thread.sleep(100);
        int i = 0;
        for (String lnk : qLinks
                ) {
            i++;
            driver.get(lnk);
            Thread.sleep(300);
            WebElement edit = driver.findElement(By.xpath("//*[@id=\"id_name\"]"));
            edit.sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE + name);
            try {
                WebElement selectElem = driver.findElement(By.id("id_coderunnertype"));
                edit.sendKeys("_" + i);
            } catch (Exception e) {

            }
            Thread.sleep(300);
            driver.findElement(By.id("id_submitbutton")).click();
            Thread.sleep(100);
        }


    }

    // не получается нормально сделать, надо потом доделать
    public void setTemplateToQuestions(String val, String template, Predicate<String> ps) throws InterruptedException {
        driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(val);

        //*[@id="single_select57f3c955e54454"]

        //driver.get(path);
        WebElement table = driver.findElement(By.id("categoryquestions"));
        List<WebElement> ql = table.findElements(By.tagName("tr"));
        ArrayList<String> qLinks = new ArrayList<>();
        Thread.sleep(100);
        for (WebElement q : ql) {
            if (ps.test(q.findElement(By.tagName("th")).getText())) {
                List<WebElement> tdList = q.findElements(By.tagName("td"));
                if (tdList.size() != 0) {
                    ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
                    qLinks.add(tds.get(2).findElement(By.tagName("a")).getAttribute("href"));
                }
            }
        }

        for (String lnk : qLinks
                ) {
            try {
                driver.get(lnk);
                Thread.sleep(300);

                WebElement chBx = driver.findElement(By.xpath("//*[@id=\"id_customise\"]"));
                if (!chBx.isSelected()) {
                    chBx.click();
                }

                Thread.sleep(300);
                WebElement templateField =
                        driver.findElement(By.xpath("//*[@class=\"felement ftextarea\"]" +
                                "/div[1]/div[1]/div[2]"));

                //System.out.println(templateField.getText());
                templateField.click();

                Thread.sleep(1000);
                driver.switchTo().activeElement().sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE + template);

                Thread.sleep(1000);
                driver.findElement(By.id("id_submitbutton")).click();
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println(e);
            }


        }

    }


    public void loadCurPage(String urlS) throws IOException {
        driver.get(urlS);
    }

    public void loadCurPageWithCloseWaiting(String urlS) throws IOException {
        driver.get(urlS);
        Boolean check = false;

        while (!check) {
            try {
                driver.getTitle();
                Thread.sleep(200);
            } catch (Exception e) {
                //you can verify correct exception here ie not reachable, dead etc..
                check = true;
            }

        }
    }

    public String getAllAteemptText(String href) throws InterruptedException {
        String s = "";
        driver.get(href);
        List<WebElement> qs = driver.findElements(By.xpath("//*[@id=\"mod_quiz_navblock\"]/div[2]/div[1]/a"));
        HashSet <String> hrefs = new HashSet<>();
        hrefs.add(href);
        for(WebElement q:qs){
            String link = q.getText();
            if(link.contains("&page"))
                hrefs.add(link);
        }
        int i =0;
        for(String link:hrefs){
            driver.get(link);
            List<WebElement> textes = driver.findElements(By.className("ablock"));
            for(WebElement text:textes){
                s+="\n--------------------------------\n";
                s+= (++i)+"\n--------------------------------\n"+ text.getText()+"\n";
            }
        }
        return s;
    }

    @Override
    public void close() throws Exception {
        driver.quit();
    }

    public void setProxy(String proxyUrl) {
        capabilities.setCapability("chrome.switches", Arrays.asList("--proxy-server=" + proxyUrl + ":8080"));
    }

    public ArrayList<WebElement> getAList(List<WebElement> l) {

        ArrayList<WebElement> al = new ArrayList<>();
        for (WebElement w : l) {
            al.add(w);
            // System.out.println(w);
        }
        return al;
    }

    public void fillRandom(String from, String to, int cnt) throws InterruptedException {
        // выбираем, куда будем переносить
        driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");
        Thread.sleep(100);
        // выбираем, откуда будем переносить
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(from);
        Thread.sleep(100);
        qselectElem = driver.findElement(By.id("menucategory"));
        qselect = new Select(qselectElem);
        qselect.selectByValue(to);

        WebElement table = driver.findElement(By.id("categoryquestions"));
        ArrayList<WebElement> ql = new ArrayList<>(table.findElements(By.tagName("tr")));

        Random random = new Random();
        HashSet<String> hs = new HashSet<>();

        for (int i = 0; i < cnt; i++) {
            WebElement w = ql.get(random.nextInt(ql.size()));
            String s = w.findElement(By.tagName("th")).getText();
            while (hs.contains(s)) {
                w = ql.get(random.nextInt(ql.size()));
                s = w.findElement(By.tagName("th")).getText();
            }
            hs.add(s);
        }
        //for (String s : hs) {
        //copyQuestion(from, as -> as.equals(s));

        moveCopiedQuestionsS(from, to, as -> hs.contains(as));
        // }
        System.out.println("Checked");
        Thread.sleep(100);
        driver.findElement(By.xpath("//input[@name=\"move\"]")).click();
        //Thread.sleep(60000);

    }

    // from - value соответствуещего пункта select'a для выбора "откуда"
    // to - value соответствуещего пункта select'a для выбора "куда"
    // ip  - лямбда выражение для выбора, какие пункты надо копировать
    public void moveCopiedQuestionsS(String from, String to, Predicate<String> sp) throws InterruptedException {
        // выбираем, куда будем переносить
        driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");

        Thread.sleep(100);
        // выбираем, откуда будем переносить
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(from);
        Thread.sleep(100);
        qselectElem = driver.findElement(By.id("menucategory"));
        qselect = new Select(qselectElem);
        qselect.selectByValue(to);


        //*[@id="single_select57f3c955e54454"]
        // Thread.sleep(1000);
        //driver.get(path);
        // Thread.sleep(1500);

        WebElement table = driver.findElement(By.id("categoryquestions"));
        List<WebElement> ql = table.findElements(By.tagName("tr"));

        int i = 0;
        for (WebElement q : ql) {
            if (sp.test(q.findElement(By.tagName("th")).getText())) {
                List<WebElement> tdList = q.findElements(By.tagName("td"));
                if (tdList.size() != 0) {
                    ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
                    try {
                        tds.get(0).findElement(By.tagName("input")).click();
                        //System.out.println(tds.get(0));
                    } catch (Exception e) {
                        System.out.println("error:" + e);
                    }

                }
            }
            i++;
        }
        System.out.println("Checked");
        Thread.sleep(100);
        try {
            driver.findElement(By.xpath("//input[@name=\"move\"]")).click();
        } catch (Exception e) {
            System.out.println(e + "");
        }
        //Thread.sleep(60000);

    }

    public void addQuestionsToTest(String path, String from) throws InterruptedException {
        // выбираем, куда будем переносить
        driver.get(path);
        Thread.sleep(100);
        // выбираем, откуда будем переносить
        driver.findElement(By.xpath("//*[@id=\"settingsnav\"]/ul/li[1]/ul/li[4]/p/a")).click();
        Thread.sleep(4000);
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(from);

        ArrayList<WebElement> aw = new ArrayList<>(driver.findElements(By.xpath("//*[@id=\"categoryquestions\"]/tbody/tr")));
        aw.get(aw.size() - 1).findElement(By.xpath("td[1]/a")).click();
        Thread.sleep(2000);
        aw = new ArrayList<>(driver.findElements(By.xpath("//*[@id=\"categoryquestions\"]/tbody/tr")));

        //System.out.println(aw);
        for (WebElement a : aw.subList(0, aw.size() - 1)) {
            List<WebElement> tdList = a.findElements(By.tagName("td"));
            //for(WebElement w:tdList){
            //  System.out.println(w.getText());
            //}

            ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
            try {
                tds.get(1).findElement(By.tagName("input")).click();
                //System.out.println(tds.get(0));
            } catch (Exception e) {
                System.out.println("error" + from);
            }

        }
        driver.findElement(By.xpath("//*[@id=\"module\"]/div/div/form[2]/fieldset/div[3]/input[1]")).click();

        Thread.sleep(1000);
    }


    public void addRef(String from, String name) throws InterruptedException {
        // выбираем, куда будем переносить
        driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");

        Thread.sleep(100);
        // выбираем, откуда будем переносить
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(from);
        Thread.sleep(100);
        driver.findElement(By.xpath("//*[@id=\"region-main\"]/div/div/div[1]/div[1]/form/div/input[1]")).click();
        Thread.sleep(100);
        driver.findElement(By.xpath("//*[@id=\"qtype_qtype_description\"]")).click();
        driver.findElement(By.name("submitbutton")).click();
        Thread.sleep(100);
        driver.findElement(By.xpath("//*[@id=\"id_name\"]")).sendKeys(name);
        Thread.sleep(5000);
        driver.findElement(By.xpath("//*[@id=\"id_questiontexteditable\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"id_questiontexteditable\"]")).sendKeys(
                "Порядок выставления оценок:\n" +
                        "1 задач - 1\n" +
                        "2 задач - 2\n" +
                        "3 задач - 3\n" +
                        "4 задач - 4\n" +
                        "5 задач - 5"
        );
        Thread.sleep(5000);
        try {
            driver.findElement(By.name("submitbutton")).click();
        } catch (Exception e) {
            System.out.println(e);
        }
        //  System.out.println("sdf");
        // Thread.sleep(20000);
    }

    public void moveCopiedQuestions(String from, String to, IntPredicate ip) throws InterruptedException {
        // выбираем, куда будем переносить
       driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");
       // driver.get("http://mdl.sch239.net/question/edit.php?cmid=1015&cat=4%2C1&qpage=0&cmid=1015&cat=4%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0&category=631%2C428&qbshowtext=0&recurse=0&recurse=1&showhidden=0&showhidden=1");

        Thread.sleep(100);
        // выбираем, откуда будем переносить
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(from);
        Thread.sleep(100);
        qselectElem = driver.findElement(By.id("menucategory"));
        qselect = new Select(qselectElem);
        qselect.selectByValue(to);


        //*[@id="single_select57f3c955e54454"]
        // Thread.sleep(1000);
        //driver.get(path);
        // Thread.sleep(1500);

        WebElement table = driver.findElement(By.id("categoryquestions"));
        List<WebElement> ql = table.findElements(By.tagName("tr"));

        int i = 0;
        for (WebElement q : ql) {
            List<WebElement> tdList = q.findElements(By.tagName("td"));
            if (tdList.size() != 0) {
                ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
                if (ip.test(i)) {
                    try {
                        tds.get(0).findElement(By.tagName("input")).click();
                        //System.out.println(tds.get(0));
                    } catch (Exception e) {
                        System.out.println("error" + from);
                    }
                }
            }
            i++;
        }
        System.out.println("Checked");
        Thread.sleep(100);
        driver.findElement(By.xpath("//input[@name=\"move\"]")).click();
        //Thread.sleep(60000);

    }


    public void loginToAnichkov() throws InterruptedException {
        driver.get("https://dogovor.anichkov.ru/");
        //Thread.sleep(1000);
        driver.findElement(By.name("username")).sendKeys("nauka");
        driver.findElement(By.name("password")).sendKeys("nauka*B61");
        driver.findElement(By.xpath("//*[@id=\"contentC\"]/center/form/table/tbody/tr[3]/td[2]/input")).click();

    }

    public void goToSamsungPage(String group) {
        driver.findElement(By.xpath("//*[@id=\"contentL\"]/div/form/select")).click();
        driver.findElement(By.xpath("//*[@id=\"contentL\"]/div/form/select/option[2]")).click();
        driver.findElement(By.xpath("//*[@id=\"contentL\"]/div/form/select/option[39]")).click();
        switch (Integer.parseInt(group)) {
            case 1:
                driver.findElement(By.xpath("//*[@id=\"contentL\"]/div/form/select/option[40]")).click();

                break;
            case 2:
                driver.findElement(By.xpath("//*[@id=\"contentL\"]/div/form/select/option[41]")).click();
                break;
            case 3:
                driver.findElement(By.xpath("//*[@id=\"contentL\"]/div/form/select/option[42]")).click();
                break;
        }
    }

    public void fillAnichkov(ArrayList<String> lst) throws InterruptedException {
        fillAnichkov(lst.get(1), lst.get(2),
                lst.get(3), lst.get(4),
                lst.get(5), lst.get(6),
                lst.get(7), lst.get(8),
                lst.get(9), lst.get(10),
                lst.get(11), lst.get(12),
                lst.get(13), lst.get(14),
                lst.get(15), lst.get(16));
    }

    public void fillAnichkov(String surname, String name, String secondmame, String sex,
                             String old, String bDate, String klass, String school, String email, String phone, String district,
                             String eName, String eSurname, String eSecondName, String adress, String ePhone
    ) throws InterruptedException {
        //Thread.sleep(1000);
        System.out.println(name + " " + surname);

        HashMap<String, String> hm = new HashMap();
        hm.put("Адмиралтейский", "1");
        hm.put("Василеостровский", "2");
        hm.put("Выборгский", "3");
        hm.put("Калининский", "4");
        hm.put("Кировский", "5");
        hm.put("Колпинский", "6");
        hm.put("Красногвардейский", "7");
        hm.put("Красносельский", "8");
        hm.put("Кронштадтский", "9");
        hm.put("Курортный", "10");
        hm.put("Московский", "11");
        hm.put("Невский", "12");
        hm.put("Пушкинский", "13");
        hm.put("Петроградский", "14");
        hm.put("Петродворцовый", "15");
        hm.put("Приморский", "16");
        hm.put("Фрунзенский", "17");
        hm.put("Центральный", "18");


        driver.findElement(By.xpath("//*[@id=\"contractDate\"]")).sendKeys("01.10.2016");
        driver.findElement(By.xpath("//*[@id=\"firstDate\"]")).sendKeys("01.10.2016");
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[2]/table/tbody/tr[3]/td[2]/input")).sendKeys(name);
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[2]/table/tbody/tr[4]/td[2]/input")).sendKeys(surname);
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[2]/table/tbody/tr[5]/td[2]/input")).sendKeys(secondmame);

        // Выбираем пол
        WebElement qselectElem = driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[2]/table/tbody/tr[6]/td[2]/select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(sex.equals("Мужской") ? "1" : "2");
        // Выбираем возраст
        qselectElem = driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[2]/table/tbody/tr[7]/td[2]/select"));
        qselect = new Select(qselectElem);
        // qselect.selectByVisibleText((Integer.parseInt(old)-2)+"");
        qselect.selectByVisibleText((old));
        // др
        driver.findElement(By.xpath("//*[@id=\"birthday\"]")).sendKeys(bDate);
        //класс
        qselectElem = driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[2]/table/tbody/tr[9]/td[2]/select"));
        qselect = new Select(qselectElem);
        qselect.selectByVisibleText((Integer.parseInt(klass) - 1) + "");
        // школа
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[3]/table/tbody/tr[1]/td[2]/input")).sendKeys(school);
        // email
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[3]/table/tbody/tr[2]/td[2]/input")).sendKeys(email);
        // телефон
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[3]/table/tbody/tr[3]/td[2]/input")).sendKeys(phone);

        qselectElem = driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[3]/table/tbody/tr[4]/td[2]/select"));
        qselect = new Select(qselectElem);
        //System.out.println(district);
        if (hm.containsKey(district)) {
            qselect.selectByValue(hm.get(district));
        } else {
            qselect.selectByValue("19");
        }

        //*[@id="formDogovor"]/div[3]/table/tbody/tr[4]/td[2]/select


        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[3]/table/tbody/tr[5]/td[2]/input")).sendKeys("1");
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[3]/table/tbody/tr[6]/td[2]/input")).sendKeys(Keys.BACK_SPACE + "4");
        // родители
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[7]/table/tbody/tr[1]/td[2]/input")).sendKeys(eName);
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[7]/table/tbody/tr[2]/td[2]/input")).sendKeys(eSurname);
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[7]/table/tbody/tr[3]/td[2]/input")).sendKeys(eSecondName);
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[8]/table/tbody/tr[1]/td[2]/input")).sendKeys(adress);
        driver.findElement(By.xpath("//*[@id=\"formDogovor\"]/div[8]/table/tbody/tr[2]/td[2]/input")).sendKeys(ePhone);


        //*[@id="formDogovor"]/div[3]/table/tbody/tr[2]/td[2]/input
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"submitDogovor\"]")).click();
    }

    // qName - value соответствуещего пункта select'a
    public void copyQuestion(String qName, Predicate<String> ps) throws InterruptedException {
        driver.get("http://mdl.sch239.net/question/edit.php?courseid=44&cat=558%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0");
       // driver.get("http://mdl.sch239.net/question/edit.php?cmid=1015&cat=4%2C1&qpage=0&cmid=1015&cat=4%2C1&qpage=0&recurse=1&showhidden=1&qbshowtext=0&category=631%2C428&qbshowtext=0&recurse=0&recurse=1&showhidden=0&showhidden=1");
        WebElement qselectElem = driver.findElement(By.tagName("select"));
        Select qselect = new Select(qselectElem);
        qselect.selectByValue(qName);
        //*[@id="single_select57f3c955e54454"]

        //driver.get(path);
        WebElement table = driver.findElement(By.id("categoryquestions"));
        List<WebElement> ql = table.findElements(By.tagName("tr"));
        ArrayList<String> qLinks = new ArrayList<>();
        Thread.sleep(100);
        for (WebElement q : ql) {
            if (ps.test(q.findElement(By.tagName("th")).getText())) {
                List<WebElement> tdList = q.findElements(By.tagName("td"));
                if (tdList.size() != 0) {
                    ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
                    qLinks.add(tds.get(3).findElement(By.tagName("a")).getAttribute("href"));
                }
            }
        }
        Thread.sleep(100);
        for (String lnk : qLinks
                ) {
            driver.get(lnk);
            Thread.sleep(300);
            WebElement selectElem = driver.findElement(By.id("id_coderunnertype"));
            Select select = new Select(selectElem);
            select.selectByValue("java_program");
            try {
                driver.switchTo().alert().accept();
            } catch (Exception e) {
                // System.out.println(e+"");
            }
            Thread.sleep(300);
            driver.findElement(By.id("id_submitbutton")).click();
            Thread.sleep(100);
        }
    }


    public static DateTime getDateTimeFromMdl(String mdl) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM y HH:mm").withLocale(Locale.ENGLISH);
        DateTime dt = formatter.parseDateTime(mdl);
        return dt;
    }

    public ArrayList<Attempt> getAttempts(String attemptName, boolean flgCode) throws InterruptedException {

        driver.get("http://mdl.sch239.net/course/view.php?id=44");
        Thread.sleep(1000);
        WebElement wTest = driver.findElement(By.xpath("//*[text()='" + attemptName + "']"));

        wTest.click();
        WebElement wAppempt = driver.findElement(By.xpath("//*[@id='region-main']/div/div[2]/a"));
        wAppempt.click();
        //Thread.sleep(2000);

        WebElement elem = driver.findElement(By.id("attempts"));
        List<WebElement> trs = elem.findElements(By.tagName("tr"));
        trs = trs.subList(0, trs.size() - 2);
        ArrayList<Attempt> aLst = new ArrayList<>();
        try (DBHelper dbHelper = new DBHelper()) {
            dbHelper.connect();

            int taskId = dbHelper.getIdByTaskName(attemptName);
            if (taskId == -1) {
                System.out.println("Тест не найден");
                return aLst;
            }

            for (WebElement tr : trs) {
                List<WebElement> tdList = tr.findElements(By.tagName("td"));
                if (tdList.size() != 0) {
                    ArrayList<WebElement> tds = new ArrayList<WebElement>(tdList);
                    if (tds.size() > 1) {
                        ArrayList<WebElement> hrLst = new ArrayList<WebElement>(tds.get(1).findElements(By.tagName("a")));
                        if (hrLst.size() == 2) {

                            String name = hrLst.get(0).getText();

                            int task_id = 0;
                            String href = hrLst.get(1).getAttribute("href");
                            Attempt at;
                            int usId = dbHelper.getIdByStudentName(CustomOperations.reverseName(name));
                            if (usId == -1) {
                                System.out.println("Пользователь не найден");
                                continue;
                            }
                            System.out.println(tds+" "+flgCode);
                            if (flgCode) {

                                ArrayList<WebElement> attemptList = getAList(tds.subList(8, tds.size()));
                                 double sum = 0;
                                for (WebElement w : attemptList) {
                                    String s = w.getText();
                                    Pattern p = Pattern.compile("[а-я]");
                                    Matcher m = p.matcher(s);
                                    System.out.println(s);
                                    if (!m.find()&&!s.equals("-"))
                                        sum += Double.parseDouble(s.replace(",", "."));

                                }
                               // System.out.println(sum);
                                at = new Attempt(
                                        task_id,
                                        tds.get(3).getText(),
                                        getDateTimeFromMdl(tds.get(4).getText()),
                                        tds.get(5).getText().equals("-")?new DateTime(0):getDateTimeFromMdl( tds.get(5).getText()),
                                        tds.get(6).getText(),
                                        tds.get(7).getText(),
                                        href,
                                        sum,
                                        usId,
                                        DateTime.now());
                            } else {
                                at = new Attempt(
                                        task_id,
                                        tds.get(3).getText(),
                                        WebSelenium.getDateTimeFromMdl(tds.get(4).getText()),
                                        tds.get(5).getText().equals("-")?new DateTime(0):getDateTimeFromMdl( tds.get(5).getText()),
                                        tds.get(6).getText(),
                                        tds.get(7).getText(),
                                        href,
                                        0,
                                        usId,
                                        DateTime.now());
                                System.out.println(tds.get(7).getText());
                            }
                            aLst.add(at);

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aLst;
    }
}
