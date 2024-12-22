//import com.fazecast.jSerialComm.SerialPort;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class ManageurDesTravailleurs {
    Scanner scanner=null;
    ArrayList<Travailleur> listDesTravailleurs= new ArrayList<Travailleur>();
    ArrayList<RFIDevenement> log = new ArrayList<RFIDevenement>();
    SerialReader rfidReader=null;

    File logFile=null;
    FileWriter logFileWriter=null;
    BufferedWriter logBufferedWriter=null;

    File bilanFile=null;
    FileWriter bilanFileWriter=null;
    BufferedWriter bilanBufferedWriter=null;
    private int choice;
    public boolean setUplistDesTravailleurs() {
        try {
            FileReader fr = new FileReader("src/travaileur.csv");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                listDesTravailleurs.add(new Travailleur(Long.parseLong(data[0]),data[1],data[2],Integer.parseInt(data[3])));
            }
            br.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    private boolean createLogFile() {
        Calendar date = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append("src/");
        builder.append(String.format("LOG_%04d-%02d-%02d",date.get(Calendar.YEAR) ,date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH)));
        builder.append(".csv");
        logFile = new File(builder.toString());
        try {
            logFileWriter = new FileWriter(logFile);
            logBufferedWriter = new BufferedWriter(logFileWriter);
            logBufferedWriter.write("ID,STATUS,HEURE\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    private void writeToLogFile(RFIDevenement event) {
        StringBuilder builder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        builder.append(String.format("%d,%s,%s%n",event.getId(),event.getStatus().getStatus(),event.getTime().format(formatter)));
        try{logBufferedWriter.write(builder.toString());} catch(IOException e){ System.out.println(e.getMessage());}
    }
    private void writeBilan(){
        Calendar date = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append("src/");
        builder.append(String.format("BILAN_%04d-%02d-%02d",date.get(Calendar.YEAR) ,date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH)));
        builder.append(".csv");
        DateTimeFormatter formatter;
        bilanFile = new File(builder.toString());
        try {
            bilanFileWriter = new FileWriter(bilanFile);
            bilanBufferedWriter = new BufferedWriter(bilanFileWriter);
            bilanBufferedWriter.write("ID,ENTRER,SORTIE,STATUS\n");
            formatter = DateTimeFormatter.ofPattern("HH:mm");
            for (Travailleur tr : listDesTravailleurs) {
                builder = new StringBuilder();
                builder.append(tr.getId());
                builder.append(",");
                if (tr.isTravailComencer()){
                    builder.append(tr.getEnter().format(formatter));
                    builder.append(",");
                    if (tr.isTravailTerminer()){
                        builder.append(tr.getSortie().format(formatter));
                        builder.append(",");
                        if (tr.getEnter().getHour()<9)builder.append("ENRER A L'HEURE ");
                        else builder.append("ENRER EN RETARD ");
                        if (tr.getSortie().getHour()>=16) builder.append("SORTIE A L'HEURE ");
                        else builder.append("SORTIE EN AVANCE ");
                    } else {
                        builder.append(",ABSENT");
                    }
                } else {
                    builder.append(",,ABSENT");
                }
                builder.append("\n");
                bilanBufferedWriter.write(builder.toString());
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("\nFailed to save bilan\n");
        }

    }
    public void start(){
        rfidReader = new SerialReader();
        if(!rfidReader.setSerialPort()) {
            System.out.println("Erreur de connexion");
            return;
        }
        createLogFile();
        boolean exit=false;
        while(!exit) {
            printMenu();
            choice = getChoice();
            switch(choice) {
                case 1:afficherBilan();
                    break;
                case 2:afficherLog();
                    break;
                case 0:
                    // save bilan
                    writeBilan();
                    try {
                        logBufferedWriter.close();
                        bilanBufferedWriter.close();
                    } catch (IOException e) {}
                    exit=true;
                    break;
                    default:System.out.println("Entrer un choix valid!");
            }
            try {
                SerialReader.serialPort.closePort();
            } catch (SerialPortException e) {}
        }
    }
    private void printMenu() {
        System.out.println("==========================================");
        System.out.println("\tManageur de travailleurs\n");
        System.out.println("1. voir le bilan");
        System.out.println("2. voir log");
        System.out.println("0. fermer le programme et sauvegarder le bilan");

    }
    private void afficherBilan(){
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("                                 LE BILAN                                                ");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-15s | %-10s | %-10s | %-10s |%n","ID","NOM","PRENOM","TELEPHONE","ENTRER","SORTIE");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Travailleur t : listDesTravailleurs) {
            String entrer="/";
            String sortie="/";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            if (t.isTravailComencer()) entrer = t.getEnter().format(formatter);
            if (t.isTravailTerminer()) sortie = t.getSortie().format(formatter);
            System.out.printf("| %10d | %-20s | %-15s | %010d | %-10s | %-10s |%n",t.getId(),t.getNom(),t.getPrenom(),t.getNumero(),entrer,sortie);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }
    private void afficherLog(){
        System.out.println("-----------------------------------");
        System.out.println("             LE LOG                ");
        System.out.println("-----------------------------------");
        System.out.printf("| %-10s | %-20s | %-5s |%n","ID","STATUS","HEURE");
        for (RFIDevenement t : log) {
            System.out.printf("| %010d | %-20s | %-5s%n",t.getId(),t.getStatus(),t.getTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        System.out.println("-----------------------------------");
    }
    private void addToLog(long id ,LocalTime time){
        //int i = Integer.parseInt(id,16);
        RFIDevenement event = new RFIDevenement();
        event.setId(id);
        event.setTime(time);
        RFIDevenement.evenementStatus stat;
        boolean found=false;
        Travailleur travailleur=null;
        for (Travailleur t : listDesTravailleurs) {
            if (t.getId() == id) {
                travailleur = t;
                found = true;
                break;
            }
        }
        if (!found) {
            stat = RFIDevenement.evenementStatus.ID_INCONUE;
        } else {
            if (time.getHour() < 10) {
                if (travailleur.isTravailComencer()) stat = RFIDevenement.evenementStatus.DEJA_ENTRER;
                else stat = RFIDevenement.evenementStatus.ENTRER_VALIDER;
            } else {
                if (travailleur.isTravailTerminer()) stat = RFIDevenement.evenementStatus.DEJA_SORTIE;
                else {
                    if (travailleur.isTravailComencer() && time.getHour() > 14)
                        stat = RFIDevenement.evenementStatus.SORTIE_VALIDER;
                    else stat = RFIDevenement.evenementStatus.SORTIE_NON_VALIDER;
                }
            }
        }
        event.setStatus(stat);
        this.log.add(event);
        printEvent(event);
        writeToLogFile(event);
        executeEvent(event,travailleur);
    }
    private void printEvent(RFIDevenement event){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.println("|                                  new event                                             |");
        System.out.printf("| ID: %010d | STATUS: %-10s | HEURE: %-5s%n",event.getId(),event.getStatus(),event.getTime().format(formatter));
        System.out.println("-----------------------------------------------------------------------------------------");
    }
    private void executeEvent(RFIDevenement event,Travailleur travailleur){
        if (travailleur==null) return;
        if (event.getStatus()==RFIDevenement.evenementStatus.ENTRER_VALIDER) {
            travailleur.setTravailComencer(true);
            travailleur.setEnter(event.getTime());
            return;
        }
        if (event.getStatus()==RFIDevenement.evenementStatus.SORTIE_VALIDER) {
            travailleur.setTravailTerminer(true);
            travailleur.setSortie(event.getTime());
            return;
        }
    }
    public int getChoice(){
        System.out.print("entrer votre choix: ");
        String inputLine=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            inputLine = br.readLine();
            if (inputLine.length()==0 || inputLine.isEmpty())return -1;
            try {
                int i = Integer.parseInt(inputLine);
                return i;
            } catch (NumberFormatException e) {
                return -1;
            }
        }catch (IOException e){
            return -1;
        }
    }
    /*
    *   Inner Classes
    * */
    class SerialReader {
        private static SerialPort serialPort;
        public boolean setSerialPort(){
            serialPort = new SerialPort("COM9");
            try {
                serialPort.openPort();
                serialPort.setParams(SerialPort.BAUDRATE_9600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                // Adding an event listener
                serialPort.addEventListener(new SerialPortEventListener() {
                    @Override
                    public void serialEvent(SerialPortEvent event) {


                            try {
                                Thread.sleep(200);
                                byte[] buffer = serialPort.readBytes(4); // Read 4 bytes
                                if (buffer != null) {
                                    StringBuilder sb = new StringBuilder();
                                    long id=0;
                                    long width=1;
                                    for (byte b : buffer) {
                                        //sb.append(String.format("%02X", b));
                                        id+=Byte.toUnsignedLong(b)*width;
                                        width*=(long)Math.pow(2,8);
                                    }
                                    //System.out.printf("rfid : %d%n",id);
                                    LocalTime time = LocalTime.now();
                                    addToLog(id,time);
                                }
                            } catch (Exception ex) {
                                //System.out.println("Error in receiving string from COM-port: " + ex);
                            }

                    }
                });
                return true;
            } catch (SerialPortException ex) {
                System.out.println("Error in setting up the serial port: " + ex);
                return false;
            }

        }

    }

    }
