package com.example;

public class PersonEditor {

    /**
     * Launches the example PersonEditor application.
     * @param args the command line arguments (ignored)
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmPerson frmPerson = new FrmPerson();
                frmPerson.setVisible(true);
            }
        });
    }

}
