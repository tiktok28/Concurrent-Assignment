import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// A class to manipulate the Excel sheet data
class SheetManipulator2 {
    XSSFSheet sheet;

    // Constructor to initialize the SheetManipulator2 with an XSSFSheet
    public SheetManipulator2(XSSFSheet sheet){
        this.sheet = sheet;
    }

    // Method to get the index of a column by its name
    public int getColumnIndex(String name) {
        int columnIndex = 0;
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            if (sheet.getRow(0).getCell(i).getStringCellValue().equals(name)) {
                columnIndex = i;
                return columnIndex;
            }
        }
        return columnIndex;
    }

    // Method to get the number of users based on the given column name (e.g., "user_id")
    public int getNumOfUsers(String name) {
        int numOfUsers = 0;
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            String user = sheet.getRow(i).getCell(getColumnIndex(name)).getStringCellValue().replaceAll("[^0-9]", "");
            if (!user.equals("")) {
                int userValue = Integer.parseInt(user);
                if (numOfUsers < userValue) {
                    numOfUsers = userValue;
                }
            }
        }
        return numOfUsers;
    }
}

// A class representing a User with their user ID and total money spent
class User2 {
    String userId;
    double userTotal = 0;

    // Constructor to initialize a User2 with a user ID
    public User2(String userId){
        this.userId = userId;
    }

    // Method to increase the total money spent by the user
    public void increaseTotal(double value){
        userTotal += value;
    }

    // Method to get the formatted user ID based on its numerical value
    public String getFormattedUserId(){
        if(Integer.parseInt(userId) >= 10){
            return String.format("USR00" + userId);
        }
        else{
            return String.format("USR000" + userId);
        }
    }

    // Method to get the formatted user total with two decimal places
    public String getFormattedUserTotal(){
        return String.format("%.2f", userTotal);
    }
}

// A class representing a thread that reads data from the Excel sheet and calculates the total money spent by each user
class Reader2 implements Runnable {
    User2[] users;
    XSSFSheet sheet;
    SheetManipulator2 sheetManipulator;

    // Constructor to initialize a Reader2 with the necessary data
    public Reader2(XSSFSheet sheet, SheetManipulator2 sheetManipulator, User2[] users){
        this.sheet = sheet;
        this.sheetManipulator = sheetManipulator;
        this.users = users;
    }

    @Override
    public void run(){
        // Read data from the Excel sheet and calculate the total money spent by each user
        int userIdIndex = sheetManipulator.getColumnIndex("user_id");
        int sharePriceIndex = sheetManipulator.getColumnIndex("share_price");
        int sharesBoughtIndex = sheetManipulator.getColumnIndex("share_bought");

        // Iterate through each row of the sheet and calculate user totals
        for (int i =0; i < sheet.getPhysicalNumberOfRows(); i++) {
            for(User2 user : users) {
                if (sheet.getRow(i).getCell(userIdIndex).getStringCellValue().equals(user.getFormattedUserId())){
                    user.increaseTotal(sheet.getRow(i).getCell(sharePriceIndex).getNumericCellValue()*sheet.getRow(i).getCell(sharesBoughtIndex).getNumericCellValue());
                }
            }
        }
    }
}

public class Q2singleT {
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            File file = new File("src\\18102673.xlsx");   //creating a new file instance
            System.out.println(file.getAbsolutePath());
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
            XSSFWorkbook wb = new XSSFWorkbook(fis);          //creating Workbook instance that refers to .xlsx file
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            SheetManipulator2 sheetManipulator = new SheetManipulator2(sheet);
            int numOfUsers = sheetManipulator.getNumOfUsers("user_id");
            User2[] users = new User2[numOfUsers];
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            for (int i = 0; i < numOfUsers; i++) {
                users[i] = new User2(String.valueOf((i+1)));
            }
            executorService.submit(new Reader2(sheet, sheetManipulator, users));
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Waiting for all tasks to complete
            }
            //Calculates total money spent by looping through users array and adding together each and every user's total
            double total = 0;
            System.out.println("user_id " + " total_money_spent_by_each_user");
            for (int i = 0; i < numOfUsers; i++){
                System.out.println(users[i].getFormattedUserId() + ": " + users[i].getFormattedUserTotal());
                total += Double.parseDouble(users[i].getFormattedUserTotal());
            }
            System.out.printf("TOTAL: %.2f", total);
            System.out.println();
            long end = System.currentTimeMillis();
            long timeTaken = end - start;
            System.out.println("Time taken: " + timeTaken + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
