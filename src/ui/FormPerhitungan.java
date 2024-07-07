/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import database.Koneksi;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import java.util.Arrays;
import popup.PopHasilPerhitungan;

/**
 *
 * @author Dewi_Pamungkas
 */
public class FormPerhitungan extends javax.swing.JFrame {

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    private DefaultTableModel tb;
    String id = "";
    int jmlAlternatif = 0;
    int jmlKriteria = 0;
    int jmlBobot = 0;
    double[][] data;
    String[] pemain;
    String[] alternatif;
    double[] kriteria;
    public double[] preferensiHitung;

    /**
     * Creates new form FormBarang
     */
    public FormPerhitungan() {
        initComponents();
        conn = Koneksi.kon();
        tb = new DefaultTableModel();
        //tabel();
        addKriteria();
        getData();
        getDataTable();
    }

    private void tabel() {
        tb.addColumn("Kode Kriteria");
        tb.addColumn("Nama Kriteria");
        tb.addColumn("Jenis Kriteria");
        tb.addColumn("Bobot");
        tabelPerhitungan.setModel(tb);
    }

//    public void kosongkanFild() {
//        tfNama.setText("");
//        tfBobot.setText("");
//    }
    public void addKriteria() {
        try {
            Statement stat = (Statement) Koneksi.kon().createStatement();
            String sql = "Select * from kriteria ORDER BY bobot DESC";
            ResultSet res = stat.executeQuery(sql);

            tb.addColumn("Nama Alternatif");
            while (res.next()) {
                tb.addColumn(res.getString("nama_kriteria"));
                tabelPerhitungan.setModel(tb);
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }
    }

    public void getData() {
        tb.getDataVector().removeAllElements();
        tb.fireTableDataChanged();

        try {
            Statement stat = (Statement) Koneksi.kon().createStatement();
            String sql = "SELECT * FROM siswa ORDER BY nama ASC";
            ResultSet res = stat.executeQuery(sql);

            while (res.next()) {
                Object[] obj = new Object[5];
                obj[0] = res.getString("nama");
                tb.addRow(obj);
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }
    }

    public void getDataTable() {
        try {
            Statement stat = (Statement) Koneksi.kon().createStatement();
            String sql = "SELECT COUNT(*) as jml_alternatif FROM siswa";
            ResultSet res = stat.executeQuery(sql);
            res.next();
            jmlAlternatif = res.getInt("jml_alternatif");
            System.out.println(res.getInt("jml_alternatif"));
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }

        try {
            Statement stat = (Statement) Koneksi.kon().createStatement();
            String sql = "SELECT COUNT(*) as jml_kriteria FROM kriteria";
            ResultSet res = stat.executeQuery(sql);
            res.next();
            jmlKriteria = res.getInt("jml_kriteria");
            System.out.println(res.getInt("jml_kriteria"));
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }

        data = new double[jmlAlternatif][jmlKriteria];
        pemain = new String[jmlAlternatif];
        alternatif = new String[jmlAlternatif];
        System.out.println(jmlAlternatif);

        try {
            Statement stat = (Statement) Koneksi.kon().createStatement();
            String sql = "SELECT * FROM siswa ORDER By nama";
            ResultSet res = stat.executeQuery(sql);

            int index = 0;
            while (res.next()) {
                alternatif[index] = res.getString("id");
                pemain[index] = res.getString("nama");
                index++;
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }
        System.out.println("Hasil Index : " + Arrays.deepToString(data));
    }

    public void getTable() {
        DefaultTableModel model = (DefaultTableModel) tabelPerhitungan.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        for (int i = 0; i < jmlAlternatif; i++) {
            if (i >= rowCount) {
                System.err.println("Row index out of bounds: " + i);
                continue; // atau break, tergantung logika yang diinginkan
            }
            for (int j = 1; j < jmlKriteria + 1; j++) {
                if (j >= columnCount) {
                    System.err.println("Column index out of bounds: " + j);
                    continue; // atau break, tergantung logika yang diinginkan
                }
                Object value = model.getValueAt(i, j);
                System.out.println("hasil objek" + value);
                if (value instanceof Integer) {
                    data[i][j] = (int) value;
                } else if (value instanceof String) {
                    try {
                        data[i][j - 1] = Integer.parseInt((String) value);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing value to int: " + value);
                        data[i][j] = 0; // atau nilai default lainnya
                    }
                } else {
                    System.err.println("Unsupported data type: " + value.getClass().getName());
                    data[i][j] = 0; // atau nilai default lainnya
                }
            }
        }
    }

    public void getKriteria() {
        kriteria = new double[jmlKriteria];

        try {
            Statement stat = (Statement) Koneksi.kon().createStatement();
            String sql = "SELECT bobot, jenis_kriteria FROM kriteria ORDER BY bobot DESC";
            ResultSet res = stat.executeQuery(sql);

            int index = 0;
            while (res.next()) {
                System.out.println(res.getString("jenis_kriteria"));
                System.out.println(res.getString("bobot"));
                System.out.println("----------------------------------------");
                if (res.getString("jenis_kriteria").equals("benefit")) {
                    double benefit = res.getDouble("bobot") / 100;
                    System.out.println(benefit);
                    kriteria[index] = benefit;
                } else {
                    double cost = res.getDouble("bobot") / -100;
                    System.out.println(cost);
                    kriteria[index] = cost;
                }
                index++;
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
        }
    }

    public void hitung() {
        double[][] normalizedData = normalizeMatrix(data, kriteria);

        // Menghitung nilai preferensi
        double[] preferenceValues = calculatePreferenceValues(normalizedData, kriteria);
        preferensiHitung = calculatePreferenceValues(normalizedData, kriteria);

        // Menampilkan hasil
        for (int i = 0; i < preferenceValues.length; i++) {
            System.out.println(pemain[i] + ": " + preferenceValues[i]);
        }

        // Output the results
        System.out.println("Normalized Matrix:");
        printMatrix(normalizedData);
        System.out.println("Preference Values:");
        printArray(preferenceValues);

        System.out.println("Hasil Array Preferensi: " + Arrays.toString(preferenceValues));
        //System.out.println("Best Player: " + pemain[bestPlayerIndex]);
    }

    public static double[][] normalizeMatrix(double[][] data, double[] criteria) {
        int rows = data.length;
        int cols = data[0].length;
        double[][] normalizedMatrix = new double[rows][cols];

        for (int j = 0; j < cols; j++) {
            double max = Double.NEGATIVE_INFINITY;
            double min = Double.POSITIVE_INFINITY;

            for (int i = 0; i < rows; i++) {
                if (data[i][j] > max) {
                    max = data[i][j];
                }
                if (data[i][j] < min) {
                    min = data[i][j];
                }
            }

            for (int i = 0; i < rows; i++) {
                if (criteria[j] < 0) { // Cost criteria
                    if (data[i][j] == 0) {
                        normalizedMatrix[i][j] = 0; // Atau nilai lain yang sesuai
                    } else {
                        normalizedMatrix[i][j] = min / data[i][j];
                    }
                } else { // Benefit criteria
                    if (max == 0) {
                        normalizedMatrix[i][j] = 0; // Atau nilai lain yang sesuai
                    } else {
                        normalizedMatrix[i][j] = data[i][j] / max;
                    }
                }
            }
        }

        return normalizedMatrix;
    }

    public static double[] calculatePreferenceValues(double[][] normalizedMatrix, double[] criteria) {
        int rows = normalizedMatrix.length;
        int cols = normalizedMatrix[0].length;
        double[] preferenceValues = new double[rows];

        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += normalizedMatrix[i][j] * Math.abs(criteria[j]);
            }
            preferenceValues[i] = sum;
        }

        return preferenceValues;
    }

    private static int getBestPlayerIndex(double[] preferenceValues) {
        int bestIndex = 0;
        double max = preferenceValues[0];
        for (int i = 1; i < preferenceValues.length; i++) {
            if (preferenceValues[i] > max) {
                max = preferenceValues[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%.2f ", value);
            }
            System.out.println();
        }
    }

    private static void printArray(double[] array) {
        for (double value : array) {
            System.out.printf("%.2f ", value);
        }
        System.out.println();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rb = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panelData = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelPerhitungan = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        lblJudul = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        panelData.setBackground(new java.awt.Color(204, 204, 255));
        panelData.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Masukan Nilai", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        tabelPerhitungan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "KD Kriteria", "Nama", "Jenis", "Bobot"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelPerhitungan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPerhitunganMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelPerhitungan);

        jButton1.setText("Hitung");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDataLayout = new javax.swing.GroupLayout(panelData);
        panelData.setLayout(panelDataLayout);
        panelDataLayout.setHorizontalGroup(
            panelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDataLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        panelDataLayout.setVerticalGroup(
            panelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDataLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        lblJudul.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        lblJudul.setText("PERHITUNGAN");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/logoSekolah.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel2.setText("SISTEM PENENTUAN SISWA TERBAIK");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setText("SMP TRISOKO JAKARTA");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/tutWuri.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(jLabel3)))
                .addGap(101, 101, 101)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblJudul)
                .addGap(345, 345, 345))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3))
                    .addComponent(jLabel4))
                .addGap(15, 15, 15)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblJudul)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(panelData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tabelPerhitunganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPerhitunganMouseClicked
        int row = tabelPerhitungan.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tabelPerhitungan.getModel();

        id = model.getValueAt(row, 0).toString();
        System.out.println(id);
    }//GEN-LAST:event_tabelPerhitunganMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        getTable();
        getKriteria();
        System.out.println(Arrays.toString(pemain));
        System.out.println(Arrays.deepToString(data));
        System.out.println(Arrays.toString(kriteria));
        hitung();
        System.out.println("ke preferensi hitung : " + Arrays.toString(preferensiHitung));
        PopHasilPerhitungan hasil = new PopHasilPerhitungan();
        
        hasil.setNilai(preferensiHitung, pemain, alternatif);
        System.out.println("dijalankan");
        if (preferensiHitung != null) {
            hasil.plgn = this;
            hasil.setVisible(true);
            hasil.setResizable(false);
        } else {
            System.out.println("masih null" + preferensiHitung);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormPerhitungan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPerhitungan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPerhitungan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPerhitungan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPerhitungan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JPanel panelData;
    private javax.swing.ButtonGroup rb;
    private javax.swing.JTable tabelPerhitungan;
    // End of variables declaration//GEN-END:variables
}
