package view;

import javax.swing.*;

import business.BrandManager;
import core.Helper;
import entity.Brand;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BrandView extends Layout{
    private JPanel container;
    private JLabel lbl_brand;
    private JLabel lbl_brand_name;
    private JTextField fld_brand_name;
    private JButton btn_brand_save;
    private Brand brand;
    private BrandManager brandManager;
    public BrandView(Brand brand){
        this.brandManager = new BrandManager();
        this.brand = brand;
        this.add(container);
        this.guiInitilaze(300,200);

        if (this.brand != null){
            this.fld_brand_name.setText(this.brand.getName());
        }


        btn_brand_save.addActionListener(e ->  {
            if(Helper.isFieldEmpty(this.fld_brand_name)){
                Helper.showMsg("fill");
            }else{
                boolean result ;
                if (this.brand == null){
                    result = this.brandManager.save(new Brand(this.fld_brand_name.getText()));
                }else{
                    this.brand.setName(this.fld_brand_name.getText());
                    result = this.brandManager.update(this.brand);
                }

                if (result) {
                    Helper.showMsg("done");
                    dispose();
                }else {
                    Helper.showMsg("error");
                }
            }
        });
    }
}
