package pkgaddressbookdb;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lina
 */
public class AccessingDBMain {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            getData();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AccessingDBMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AccessingDBMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getData() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                        + "addressbook?serverTimezone=UTC&characterEncoding=UTF-8", "root", "root");) {
            int input = 0;
            while (input < 7) {
                Scanner sc = new Scanner(System.in);
                Scanner scL = new Scanner(System.in);

//if 0
                if (input == 0) {
                    System.out.println("For list of people press:" + ANSI_GREEN + " 1" + ANSI_RESET);
                    System.out.println("For list of addresses press:" + ANSI_GREEN + " 2" + ANSI_RESET);
                    System.out.println("For list of contancts press:" + ANSI_GREEN + " 3" + ANSI_RESET);
                    System.out.println("For deleting a person press:" + ANSI_GREEN + " 4" + ANSI_RESET);
                    System.out.println("For updating new person press:" + ANSI_GREEN + " 5" + ANSI_RESET);
                    System.out.println("For inserting a person press:" + ANSI_GREEN + " 6" + ANSI_RESET);
                    System.out.println("If you want to exit:" + ANSI_GREEN + " 7" + ANSI_RESET);
                    System.out.print("Your choise: ");

                    try {
                        input = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("");
                        System.out.println(ANSI_RED + "THERE IS NO SUCH OPTION!" + ANSI_RESET);
                        input = 0;
                        continue;
                    }
                    if (input < 1 || input > 7) {
                        System.out.println("");
                        System.out.println(ANSI_RED + input + " IS NOT AN OPTION!" + ANSI_RESET);
                        input = 0;
                        continue;
                    }
                }

                Set idAll = new HashSet();
                try (
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT id from person;");) {
                    while (rs.next()) {
                        idAll.add(rs.getInt("id"));
                    }
                }
                if (input == 1) {
                    try (
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery("SELECT * from person");) {
                        while (rs.next()) {
                            System.out.print(rs.getInt(1) + " "
                                    + ANSI_BLUE + rs.getString("first_name") + " "
                                    + rs.getString("last_name") + " " + ANSI_RESET
                                    + rs.getDate(4) + " "
                                    + "(salary: " + rs.getBigDecimal(5) + ")"
                                    + "\n");
                        }
                    }
                    System.out.println("..........");
                    input = 0;
//if 2                    
                } else if (input == 2) {
                    System.out.println("Enter " + ANSI_GREEN + "id-number " + ANSI_RESET + "of persons you want to get address-list of,");
                    System.out.println("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back:");
                    try {
                        input = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println(ANSI_RED + "THIS IS NOT A NUMBER!" + ANSI_RESET);
                        input = 2;
                        continue;
                    }
                    if (input == 0) {
                        continue;
                    }
                    if (!idAll.contains(input)) {
                        System.out.println(ANSI_RED + "There is no person with id number = " + input + ";" + ANSI_RESET);
                        input = 2;
                        continue;
                    }
                    try (
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery("SELECT  * from person, address "
                                    + "where person.id = address.person_id "
                                    + "and address.person_id =" + input + " "
                            // + "group by address.id;"
                            );) {
                        if (rs.next() == false) {
                            System.out.println("Person with id=" + ANSI_GREEN + input + ANSI_RESET + " has no entries in address-list;");
                        }
                        rs.previous();
                        if (rs.first()) {
                            System.out.println("Addresses of "
                                    + ANSI_BLUE
                                    + rs.getString("first_name") + " "
                                    + rs.getString("last_name")
                                    + ANSI_RESET + ":");
                            rs.previous();
                        }
                        while (rs.next()) {
                            System.out.print(ANSI_CYAN
                                    + rs.getString("address") + " "
                                    + rs.getString("city") + " "
                                    + rs.getString("postal_code") + " "
                                    + "\n" + ANSI_RESET);
                        }
                    }
                    System.out.println("..........");
                    input = 0;
//if 3                    
                } else if (input == 3) {
                    System.out.println("Enter " + ANSI_GREEN + "id-number " + ANSI_RESET + "of persons you want to get contact-list of,");
                    System.out.println("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back:");
                    try {
                        input = sc.nextInt();
                    } catch (Exception e) {
                        System.out.println(ANSI_RED + "THIS IS NOT A NUMBER!" + ANSI_RESET);
                        input = 3;
                        continue;
                    }
                    if (input == 0) {
                        continue;
                    }
                    if (!idAll.contains(input)) {
                        System.out.println(ANSI_RED + "There is no person with id number = " + input + ";" + ANSI_RESET);
                        input = 3;
                        continue;
                    }
                    try (
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery("SELECT  * from person, contact "
                                    + "where person.id = contact.person_id and"
                                    + " contact.person_id =" + input + " "
                            // + "group by contact.id;"
                            );) {
                        if (rs.next() == false) {
                            System.out.println("Person with id="
                                    + ANSI_GREEN + input + ANSI_RESET
                                    + " has no entries in contact-list;");
                        } else {
                            System.out.println("Contacts of " + ANSI_BLUE
                                    + rs.getString("first_name") + " "
                                    + rs.getString("last_name")
                                    + ANSI_RESET + ":");
                            rs.previous();
                        }
                        while (rs.next()) {
                            System.out.print(ANSI_PURPLE
                                    + rs.getString("contact_type") + " "
                                    + rs.getString("contact") + " "
                                    + "\n" + ANSI_RESET);
                        }
                    }
                    System.out.println("..........");
                    input = 0;
//if 4
                } else if (input == 4) {
                    System.out.println("Enter" + ANSI_GREEN
                            + " id-number" + ANSI_RESET
                            + " of person You want to remove from person-list:");
                    System.out.println("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back:");

                    try {
                        input = sc.nextInt();
                    } catch (Exception e) {
                        System.out.println(ANSI_RED + "THIS IS NOT A NUMBER!" + ANSI_RESET);
                        input = 4;
                        continue;
                    }
                    if (input == 0) {
                        continue;
                    }
                    if (!idAll.contains(input)) {
                        System.out.println(ANSI_RED + "There is no person with id number = " + input + ";" + ANSI_RESET);
                        input = 4;
                        continue;
                    }

                    //            sc.nextLine();
                    String name = "";
                    try (
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery("Select first_name, last_name "
                                    + "from person "
                                    + "where id=" + input);) {
                        rs.next();
                        name = (rs.getString(1) + " " + rs.getString(2));

                    }
                    try (
                            Statement st = con.createStatement();) {
                        st.execute("delete from person "
                                + "where id = " + input);
                    }
                    System.out.println(ANSI_BLUE + name + ANSI_RESET + " was deleted from the list.");
                    System.out.println("...............");
                    input = 0;
// if 5                    
                } else if (input == 5) {
                    System.out.println("Enter" + ANSI_GREEN
                            + " id-number" + ANSI_RESET
                            + " of the person You want to UPDATE:");
                    System.out.println("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back:");
                    try {
                        input = sc.nextInt();
                    } catch (Exception e) {
                        System.out.println(ANSI_RED + "THIS IS NOT A NUMBER!" + ANSI_RESET);
                        input = 5;
                        continue;
                    }
                    if (input == 0) {
                        continue;
                    }
                    if (!idAll.contains(input)) {
                        System.out.println(ANSI_RED + "There is no person with id number = " + input + ";" + ANSI_RESET);
                        input = 5;
                        continue;
                    }
                    System.out.println("Enter First Name, ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");
                    String fn = scL.nextLine();
                    try {
                        if (parseInt(fn) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore
                    }

                    System.out.println("Enter Last Name: ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");
                    String ln = scL.nextLine();
                    try {
                        if (parseInt(ln) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore
                    }

                    System.out.println("Enter Birthdate (yyyy-MM-dd): ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");
                    String date = scL.nextLine();

                    try {
                        if (parseInt(date) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore

                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date bd = new Date();
                    //       while (true) {
                    try {
                        bd = sdf.parse(date);
                    } catch (ParseException ex) {
                        System.out.println(ANSI_RED + "WRONG DATE FORMAT!" + ANSI_RESET
                        );
                        input = 6;
                        continue;
                    }
                  
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(bd);
                    cal.set(Calendar.HOUR, 12);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.ZONE_OFFSET, 0);
                    java.sql.Date bdSql = new java.sql.Date(cal.getTime().getTime()); // ka daro sitie du getTime()???

                    BigDecimal s = null;
                    System.out.println("Enter salary: ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");

                    String bigD = scL.nextLine();
                    try {
                        if (parseInt(date) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore

                    }

                    try {
                        s = new BigDecimal(bigD);
                    } catch (Exception e) {
                        System.out.println(ANSI_RED + "IS NOT A NUMBER!!" + ANSI_RESET);
                        input = 6;
                        continue;
                    }

                    try (
                            PreparedStatement st = con.prepareStatement("update person "
                                    + "set first_name = ?, last_name = ?, birth_date = ?, salary = ? "
                                    + "where id = ?");) {
                        st.setString(1, fn);
                        st.setString(2, ln);
                        st.setDate(3, bdSql);
                        st.setBigDecimal(4, s);
                        st.setInt(5, input);
                        st.execute();
                    }

                    System.out.println(fn + " " + ln + " date:" + sdf.format(bdSql) + " salary:" + s);
                    input = 0;

                } else if (input == 6) {
                    System.out.println("Enter First Name, ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");
                    String fn = scL.nextLine();

                    try {
                        if (parseInt(fn) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore

                    }

                    System.out.println("Enter Last Name: ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");
                    String ln = scL.nextLine();
                    try {
                        if (parseInt(ln) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore

                    }

                    System.out.println("Enter Birthdate (yyyy-MM-dd): ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");
                    String date = "";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date bd = null;
                   // while (bd == null) {
                        date = scL.nextLine();
                        try {
                            if (parseInt(date) == 0) {
 
                                continue;
                            }
                        } catch (Exception e) {

                        }
                        try {
                            bd = sdf.parse(date);
                        } catch (ParseException ex) {
                            System.out.println(ANSI_RED + "WRONG DATE FORMAT!" + ANSI_RESET
                                    + "\nenter required date format:" + ANSI_GREEN + " \"yyyy-MM-dd\"" + ANSI_RESET + ": "
                                    + "\nor to continue press" + ANSI_GREEN + " 0" + ANSI_RESET + ": "
                            );
                            input = 6;
                            continue;
                        }

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(bd);

                    cal.set(Calendar.HOUR, 12);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.ZONE_OFFSET, 0);

                    java.sql.Date bdSql = new java.sql.Date(cal.getTime().getTime()); // ka daro sitie du getTime()???

                    BigDecimal s = null;
                    System.out.println("Enter salary: ");
                    System.out.print("or press " + ANSI_GREEN + "0" + ANSI_RESET + " for getting back: ");

                    String bigD = scL.nextLine();

                    try {
                        if (parseInt(date) == 0) {
                            input = 0;
                            continue;
                        }
                    } catch (Exception e) {//Ignore

                    }

                    try {
                        s = new BigDecimal(bigD);
                    } catch (Exception e) {
                        System.out.println(ANSI_RED + "IS NOT A NUMBER!!" + ANSI_RESET);
                        input = 6;
                        continue;
                    }

                    try (
                            PreparedStatement st = con.prepareStatement("insert into person"
                                    + " ("
                                    + "first_name, last_name, birth_date, salary) "
                                    + "values (?, ?, ?, ?)");) {
                        st.setString(1, fn);
                        st.setString(2, ln);
                        st.setDate(3, bdSql);
                        st.setBigDecimal(4, s);
                        st.execute();
                    }

                    input = 0;
                } else if (input == 7) {
                    System.out.println("BYE BYE!!");
                }
            }
        }
    }
}
