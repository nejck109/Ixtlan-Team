package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;



public class MyCustomCalendar{
    static JFrame frame;
    static Container pane;
    static JTable tableCal;
    static JLabel lblMonthAndYear, lblMonthStatic, lblYearStatic, lblEnterDate, lblErr;
    static JComboBox cmbMonthsList;
    static JTextField txtYear, txtEnterDate;
    static JButton btnSndParams;
    static DefaultTableModel model;
    static JScrollPane scrollCal;
    static JPanel panelCal;
    static int rightYear, rightMonth, rightDay, chosenYear, chosenMonth, chosenDay;
    static List<String> dates = new ArrayList<>();


    static void componentsInit(){
        //Prepare frame
        frame = new JFrame ("Ixtlan Team Calendar");
        frame.setSize(330, 405);
        pane = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        //Create controls
        lblMonthAndYear = new JLabel ("January");
        lblYearStatic = new JLabel ("Enter year:");
        lblMonthStatic = new JLabel ("Select month:");
        lblEnterDate = new JLabel("Enter date (dd/mm/yyyy):");
        lblErr = new JLabel("Invalid data format.");
        txtYear = new JTextField();
        txtEnterDate = new JTextField();
        String [] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        cmbMonthsList = new JComboBox(months);
        btnSndParams = new JButton("Search");
        model = new DefaultTableModel();
        tableCal = new JTable(model);
        scrollCal = new JScrollPane(tableCal);
        panelCal = new JPanel(null);

        //Add to panel
        pane.add(panelCal);
        panelCal.add(lblMonthAndYear);
        panelCal.add(lblMonthStatic);
        panelCal.add(lblYearStatic);
        panelCal.add(lblEnterDate);
        panelCal.add(lblErr);
        panelCal.add(cmbMonthsList);
        panelCal.add(txtYear);
        panelCal.add(txtEnterDate);
        panelCal.add(btnSndParams);
        panelCal.add(scrollCal);

        // If clicked
        btnSndParams.addActionListener(new btnSndParams_Action());


        //Get real month/year
        GregorianCalendar cal = new GregorianCalendar();
        rightDay = cal.get(GregorianCalendar.DAY_OF_MONTH);
        rightMonth = cal.get(GregorianCalendar.MONTH);
        rightYear = cal.get(GregorianCalendar.YEAR);
        chosenMonth = rightMonth;
        chosenYear = rightYear;
    }

    static void componentsPosition(){

        // set position of elements
        panelCal.setBounds(0, 0, 320, 405);
        lblYearStatic.setBounds(170, 230, 80, 20);
        lblMonthStatic.setBounds(14, 230, 80, 20);
        lblMonthAndYear.setBounds(14, 10, 180, 25);
        lblEnterDate.setBounds(20, 270, 130, 20);
        txtEnterDate.setBounds(158, 270, 130, 20);
        txtYear.setBounds(230, 230, 70, 20);
        cmbMonthsList.setBounds(80, 230, 80, 20);
        btnSndParams.setBounds(panelCal.getWidth() / 2 - 38, 305, 75, 25);
        scrollCal.setBounds(14, 35, 285, 177);
        lblErr.setBounds(panelCal.getWidth() / 2 - 50, 333, 100, 25);
        lblErr.setVisible(false);
        lblErr.setForeground(Color.red);
    }

    static void modelCalendarInit(){

        //Add columnDay - days
        String[] columnDay = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i=0; i<7; i++){
            model.addColumn(columnDay[i]);
        }

        tableCal.setShowVerticalLines(false);

        // generate model
        tableCal.setRowHeight(25);
        model.setColumnCount(7);
        model.setRowCount(6);

        //Refresh calendar - first
        updateCalendar (rightMonth, rightYear);
    }

    static void readFromFile() throws Exception{
        File holidaysFile = new File("holidays.txt");
        FileReader fr = new FileReader(holidaysFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            dates.add(line);
        }
    }


    public static void updateCalendar(int month, int year){
        //Variables
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int numOfDays, startDay;

        lblMonthAndYear.setText(months[month] + ", " + chosenYear); //Refresh the month and year label

        //Clear data in table
        model.setRowCount(0);
        model.setRowCount(6);

        //Get first day of month(shift) and number of days in month
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        numOfDays = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        startDay = cal.get(GregorianCalendar.DAY_OF_WEEK);

        //Draw calendar / - 2 because week starts on monday
        int i = startDay - 2;
        // if index goes to -1, push to next week
        if (i == -1) i = i + 7;
        for(int day=1;day<=numOfDays;day++){
            model.setValueAt(day, i/7 , i%7);
            i = i + 1;
        }

        // Cell color
        tableCal.setShowHorizontalLines(true);
        tableCal.setDefaultRenderer(tableCal.getColumnClass(0), new tableCalRenderer());
    }

    public static void main (String args[]){

        // style
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {
            e.printStackTrace();
        }

        componentsInit();
        componentsPosition();
        modelCalendarInit();
        try {
            readFromFile();
        }catch (Exception e){
            System.out.println("No holidays file found.");
        }

    }

    // Change color renderer for each cell
    static class tableCalRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            Component cell = super.getTableCellRendererComponent(table, value, selected, focused, row, column);

            // alignment of values in cells
            setHorizontalAlignment( JLabel.CENTER );

            //Sunday
            if (column == 6) {
                cell.setBackground(new Color(190, 190, 255));
            }else{
                cell.setBackground(new Color(255, 255, 255));
            }
            // Chosen day
            if (value != null) {
                if (Integer.parseInt(value.toString()) == chosenDay) {
                    cell.setBackground(new Color(100, 255, 100));
                }
            }

            //Today
            if (value != null) {
                if (Integer.parseInt(value.toString()) == rightDay && chosenMonth == rightMonth && chosenYear == rightYear) {
                    cell.setBackground(new Color(200, 255, 200));
                }
            }
            // Holidays
            for (String date: dates) {

                // Split - ponavlja, then split the date to day, month, year
                String [] lineData = date.split("-");
                String [] splitDate = lineData[0].split("/");
                int holidayDay = Integer.parseInt(splitDate[0]);
                // month starts with 0, so we need -1.
                int holidayMonth = Integer.parseInt(splitDate[1])-1;
                int holidayYear = Integer.parseInt(splitDate[2]);

                    if (value != null) {
                        // if repeated
                        if (lineData.length == 2){
                            if (Integer.parseInt(value.toString()) == holidayDay && chosenMonth == holidayMonth) {
                                cell.setBackground(new Color(130, 215, 235));
                        }
                    }else{
                            if (Integer.parseInt(value.toString()) == holidayDay && chosenMonth == holidayMonth && chosenYear == holidayYear) {
                                cell.setBackground(new Color(130, 215, 235));
                            }

                        }
                }
            }
            return cell;
        }
    }

    // On click - Search button
    static class btnSndParams_Action implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            // full date field
            if (!txtEnterDate.getText().trim().equals("")) {
                String[] dateSplit = txtEnterDate.getText().trim().split("/");

                // Validate user input
                if (dateSplit.length == 3 && dateSplit[0].trim().matches("[0-9]+")
                && dateSplit[1].trim().matches("[0-9]+") && dateSplit[2].trim().matches("[0-9]+") && dateSplit[2].trim().length() == 4) {
                    chosenDay = Integer.parseInt(dateSplit[0]);
                    chosenMonth = Integer.parseInt(dateSplit[1]) - 1;
                    chosenYear = Integer.parseInt(dateSplit[2]);
                    if ((chosenDay > 30 && chosenMonth % 2 == 0) || (chosenDay > 31 && chosenMonth % 2 == 1) || (chosenMonth == 1 && chosenDay > 28) || chosenMonth > 11){
                        lblErr.setVisible(true);
                }else {
                        // If user enters full date - Clear other fields
                        txtYear.setText("");
                        cmbMonthsList.setSelectedIndex(0);
                        updateCalendar(chosenMonth, chosenYear);
                        lblErr.setVisible(false);
                    }
                }else {
                    lblErr.setVisible(true);
                }
            }
                // Year field
                if (!txtYear.getText().trim().equals("")) {
                    if (txtYear.getText().trim().matches("[0-9]+") && txtYear.getText().trim().length() == 4) {
                        chosenYear = Integer.parseInt(txtYear.getText().trim());
                        chosenMonth = cmbMonthsList.getSelectedIndex();
                        chosenDay = 32;
                        lblErr.setVisible(false);
                        updateCalendar(chosenMonth, chosenYear);
                    } else {
                        lblErr.setVisible(true);
                    }
                }
                // if user chooses only month
            if (txtYear.getText().trim().equals("") && txtEnterDate.getText().trim().equals("")){
                chosenMonth = cmbMonthsList.getSelectedIndex();
                chosenDay = 32;
                lblErr.setVisible(false);
                updateCalendar(chosenMonth, chosenYear);
            }

        }
    }
}