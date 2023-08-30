package com.ccc;

import com.alibaba.fastjson.JSON;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入邮箱：");
        String email = scanner.next();
        WebDriverManager.chromedriver().setup();
        ChromeDriver driver = new ChromeDriver();
        FakeStudent fakeStudent = getFakeStudent(driver);
        fakeStudent.setEmail(email);
        fakeStudent.setBirthYear(getYear());
        String month = getMonth();
        fakeStudent.setBirthMonth(month);
        fakeStudent.setBirthDay(getDay(month));
        fakeStudent.setCity("Oklahoma City");

        FakeStudent fakeStudent1 = getFakeStudent(driver);



        sign(driver,fakeStudent,fakeStudent1);

        write(fakeStudent);

    }

    public static void write(FakeStudent fakeStudent){
        String s = JSON.toJSONString(fakeStudent);
        File file = new File("d://" + fakeStudent.getEmail() + "-" + fakeStudent.getName() + ".txt");

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            fileOut.write(bytes);
            System.out.println("文件写入"+file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sign(ChromeDriver driver,FakeStudent fakeStudent,FakeStudent fakeStudent1) throws InterruptedException {
        driver.get("https://enroll.carlalbert.edu/application/");


        Select campus = null;
        while (campus == null){
            WebElement element = findElement(driver, By.id("campus"));
            if (element != null){
                campus = new Select(element);
            }
        }
        campus.selectByValue("W");
        List<WebElement> degreeSeeking = driver.findElementsByName("degreeSeeking");
        degreeSeeking.get(1).click();
        String[] nameSplit = fakeStudent.getName().split(" ");
        driver.findElementById("firstname").sendKeys(nameSplit[0]);
        if (nameSplit.length == 1){
            driver.findElementById("lastname").sendKeys("Li");
        }else if (nameSplit.length > 2){
            driver.findElementById("midname").sendKeys(nameSplit[1]);
            driver.findElementById("lastname").sendKeys(nameSplit[2]);
        }else {
            driver.findElementById("lastname").sendKeys(nameSplit[1]);
        }
        driver.findElementById("ssn").sendKeys(fakeStudent.getSSN());
        driver.findElementById("birthdate").sendKeys(fakeStudent.getFullBirthDay());
        driver.findElementById("addr1").sendKeys(fakeStudent.getAddress());
        driver.findElementById("city").sendKeys(fakeStudent.getCity());

        Select state = new Select(findElement(driver, By.id("state")));
        state.selectByValue("OK");
        driver.findElementById("zip").sendKeys(fakeStudent.getZip());
        Select county = new Select(findElement(driver, By.id("county")));
        county.selectByValue("99");

        driver.findElementById("homePhone").sendKeys(fakeStudent.getPhone());
        driver.findElementById("cellPhone").sendKeys(fakeStudent.getPhone());
        driver.findElementById("email").sendKeys(fakeStudent.getEmail());

        driver.findElementById("emergencyName").sendKeys(fakeStudent1.getName());
        driver.findElementById("emergencyAddr").sendKeys(fakeStudent.getFullAddress());
        driver.findElementById("emergencyPhone").sendKeys(fakeStudent1.getPhone());
        driver.findElementById("OKresidentTime").sendKeys("12");
        driver.findElementById("white").click();

        driver.findElementById("gradDate")
                .sendKeys("00" + (Integer.parseInt(fakeStudent.getBirthYear()) + 19) + "/04/01");

        Select hsState = new Select(findElement(driver, By.id("hsState")));
        hsState.selectByValue("OK");

        sleep(1000);

        Select hsName = new Select(findElement(driver, By.id("hsName")));
        hsName.selectByValue("371552");

        driver.findElementById("action").click();

    }
    public static FakeStudent getFakeStudent(ChromeDriver driver) throws InterruptedException {
        FakeStudent fakeStudent = new FakeStudent();
        driver.get("https://www.meiguodizhi.com/");
        driver.findElementById("city").sendKeys("俄克拉荷马州");
        String text = "";
        while (!text.equals("Oklahoma City")){
            driver.findElementById("search_btn").click();
            sleep(3000);
            findElement(driver, By.className("data_City")).click();
            text = getSysClipboardText();
        }
        //全名
        findElement(driver, By.className("data_Full_Name")).click();
        fakeStudent.setName(getSysClipboardText());
        //街道
        findElement(driver, By.className("data_Address")).click();
        fakeStudent.setAddress(getSysClipboardText());
        //邮编
        findElement(driver, By.className("data_Zip_Code")).click();
        fakeStudent.setZip(getSysClipboardText());
        //电话
        findElement(driver, By.className("data_Telephone")).click();
        fakeStudent.setPhone(getSysClipboardText());
        //SSN
        findElement(driver, By.className("data_Social_Security_Number")).click();
        fakeStudent.setSSN(getSysClipboardText());
        return fakeStudent;
    }
    public static WebElement findElement(WebDriver driver, By by) {
        try {
            WebElement element = driver.findElement(by);
            return element;
        } catch (Exception e) {
            return null;
        }
    }
    public static String getYear() {
        Random random = new Random();
        int i = random.nextInt(3);
        return String.valueOf(2001 + i);
    }
    public static String getMonth() {
        Random random = new Random();
        int i = random.nextInt(12);
        return String.valueOf(1 + i);
    }
    public static String getDay(String month) {
        Random random = new Random();
        int i = 0;
        if (month.equals("2")){
            i = random.nextInt(28);
        }else if (month.equals("4") || month.equals("6") || month.equals("9") || month.equals("11")){
            i = random.nextInt(30);
        }else {
            i = random.nextInt(31);
        }
        return String.valueOf(1 + i);
    }
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }
}