package view;

import business.BookManager;
import business.BrandManager;
import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminView  extends Layout{
    private JPanel container;
    private JLabel lbl_welcome;
    private JPanel pnl_top;
    private JTabbedPane tab_menu;
    private JButton btn_logout;
    private JPanel pnl_brand;
    private JScrollPane scl_brand;
    private JTable tbl_brand;
    private JPanel pnl_model;
    private JScrollPane scrl_model;
    private JTable tbl_model;
    private JButton btn_search_model;

    private JComboBox<ComboItem> cmb_s_model_brand;
    private JComboBox<Model.Type> cmb_s_model_type;
    private JComboBox<Model.Fuel> cmb_s_model_fuel;
    private JComboBox<Model.Gear> cmb_s_model_gear;
    private JButton btn_cncl_model;
    private JPanel pnl_car;
    private JTable tbl_car;
    private JScrollPane scl_car;
    private JPanel pnl_booking;
    private JTable tbl_booking;
    private JComboBox cmb_booking_type;
    private JComboBox cmb_booking_fuel;
    private JComboBox cmb_booking_gear;
    private JButton btn_booking_search;
    private JFormattedTextField fld_fnsh_date;
    private JFormattedTextField fld_strt_date;

    private JFormattedTextField fld_book_fnsh_date;
    private JFormattedTextField fld_book_strt_date;

    private JScrollPane scrl_booking;
    private JPanel pnl_booking_search;
    private JButton btn_cncl_booking;
    private JPanel pnl_book;
    private JButton btn_book_search;
    private JButton btn_cncl_book;
    private JComboBox cmb_book_car;
    private JScrollPane scrl_book;
    private JTable tbl_book;
    private User user;
    private DefaultTableModel tmdl_brand = new DefaultTableModel();
    private DefaultTableModel tmdl_model = new DefaultTableModel();
    private DefaultTableModel tmdl_car = new DefaultTableModel();
    private DefaultTableModel tmdl_booking = new DefaultTableModel();
    private DefaultTableModel tmdl_book = new DefaultTableModel();
    private BrandManager brandManager;
    private ModelManager modelManager;
    private CarManager carManager;
    private BookManager bookManager;
    private JPopupMenu brand_menu;
    private JPopupMenu model_menu;
    private JPopupMenu booking_menu;
    private JPopupMenu car_menu;
    private JPopupMenu book_menu;
    private Object[] col_model;
    private Object[] col_car;
    private Object[] col_book;

    public AdminView(User user) {
        this.brandManager = new BrandManager();
        this.modelManager = new ModelManager();
        this.carManager = new CarManager();
        this.bookManager = new BookManager();
        this.add(container);
        this.guiInitilaze(1000, 500);
        this.user = user;
        if (this.user == null) {
            dispose();
        }

        this.lbl_welcome.setText("Hoşgeldiniz  , " + this.user.getUsername());

        //General Code
        loadComponent();

        //Brand Tab Menu
        loadBrandTable();
        loadBrandComponent();

        //Model Tab Menu
        loadModelTable(null);
        loadModelComponent();
        loadModelFilter();

        //Car Tab Menu
        loadCarTable();
        loadCarComponent();

        //Booking Tab Menu
        loadBookingTable(null);
        loadBookingComponent();
        loadBookingFilter();

        //Book Tab Menu
         loadBookTable(null);
         loadBookComponent();
         loadBookFilterCar();

    }
        private void loadComponent () {
            this.btn_logout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    LoginView loginView = new LoginView();
                   // loginView.setVisible(true);
                }
            });
        }

        private void loadBookTable(ArrayList<Object[]> bookList){
            this.col_book = new Object[]{"ID", "Plaka", "Araç Marka", "Araç Model", "Müşteri", "Telefon", "Mail", "T.C.", "Başlangıç Tarihi", "Bitiş Tarihi", "Fiyat"};
            if (bookList == null){
                bookList= this.bookManager.getForTable(col_book.length, this.bookManager.findAll());
            }
            createTable(this.tmdl_book, this.tbl_book, col_book, bookList);
        }
        public void loadBookFilterCar(){
        this.cmb_book_car.removeAllItems();
        for (Car obj : carManager.findAll()){
            this.cmb_book_car.addItem(new ComboItem(obj.getId(), obj.getPlate()));
        }
        this.cmb_book_car.setSelectedItem(null);
    }
        private void loadBookComponent(){
        tableRowSelect(this.tbl_book);
        this.book_menu = new JPopupMenu();
        this.book_menu.add("İptal Et").addActionListener(e -> {
            if (Helper.confirm("sure")){
                int selectBookId = getTableSelectedRow(this.tbl_book, 0);
                if (this.bookManager.delete(selectBookId)) {
                    Helper.showMsg("done");
                    loadBookTable(null);
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_book.setComponentPopupMenu(book_menu);
        btn_book_search.addActionListener(e -> {
            ComboItem selectedCar = (ComboItem) this.cmb_book_car.getSelectedItem();
            int carId = 0;
            if (selectedCar != null){
                carId = selectedCar.getKey();
            }
            ArrayList<Book> bookListBySearch = this.bookManager.searchForTable(carId);
            ArrayList<Object[]> bookRowListBySearch = this.bookManager.getForTable(this.col_book.length, bookListBySearch);
            loadBookTable(bookRowListBySearch);
        });
        this.btn_cncl_book.addActionListener(e -> {
            loadBookFilterCar();
        });


    }

        private void loadBookingComponent(){
        tableRowSelect(this.tbl_booking);
        this.booking_menu = new JPopupMenu();
        this.booking_menu.add("Rezervasyon Yap").addActionListener(e -> {

            int selectCarId = this.getTableSelectedRow(this.tbl_booking, 0);

            BookingView bookingView = new BookingView(
                    this.carManager.getById(selectCarId),
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText()
            );
            bookingView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBookingTable(null);
                    loadBookingFilter();
                }
            });
        });

        this.tbl_booking.setComponentPopupMenu(booking_menu);

        btn_booking_search.addActionListener(e ->  {

            ArrayList<Car> carList = this.carManager.searchForBooking(
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText(),
                    (Model.Type) this.cmb_booking_type.getSelectedItem(),
                    (Model.Gear) this.cmb_booking_gear.getSelectedItem(),
                    (Model.Fuel) this.cmb_booking_fuel.getSelectedItem());

            ArrayList<Object[]> carBookingRow = this.carManager.getForTable(this.col_car.length, carList);
            loadBookingTable(carBookingRow);
        });
        btn_cncl_booking.addActionListener(e -> {
            loadBookingFilter();
        });
    }
        private void loadBookingTable(ArrayList<Object[]> carList){
        Object[] col_booking_list = {"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt", "Vites"};
        createTable(this.tmdl_booking, this.tbl_booking, col_booking_list, carList);
    }
        public void loadBookingFilter(){
        this.cmb_booking_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_booking_type.setSelectedItem(null);
        this.cmb_booking_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_booking_gear.setSelectedItem(null);
        this.cmb_booking_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_booking_fuel.setSelectedItem(null);
    }

        private void loadCarComponent() {
        tableRowSelect(this.tbl_car);
        this.car_menu = new JPopupMenu();
        this.car_menu.add("Yeni").addActionListener(e -> {
            CarView carView = new CarView(new Car());
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                }
            });
        });
            this.car_menu.add("Güncelle").addActionListener(e -> {
                int selectModelId = this.getTableSelectedRow(this.tbl_car, 0);
                CarView carView = new CarView(this.carManager.getById(selectModelId));
                carView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadCarTable();
                    }
                });
            });
                this.car_menu.add("Sil").addActionListener(e -> {
                    if (Helper.confirm("sure")){
                        int selectCarId = getTableSelectedRow(this.tbl_car, 0);
                        if (this.carManager.delete(selectCarId)) {
                            Helper.showMsg("done");
                            loadCarTable();
                        }else{
                            Helper.showMsg("error");
                        }
                    }
                });
            this.tbl_car.setComponentPopupMenu(car_menu);
    }
        public void loadCarTable(){
        col_car = new Object[]{"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt", "Vites"};
        ArrayList<Object[]> carList = carManager.getForTable(col_car.length, this.carManager.findAll());
        createTable(this.tmdl_car, this.tbl_car, col_car, carList);
    }

        private void loadModelComponent() {
        tableRowSelect(this.tbl_model);
        this.model_menu = new JPopupMenu();
        this.model_menu.add("Yeni").addActionListener(e -> {
            ModelView modelView = new ModelView(new Model());
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                }
            });
        });
        this.model_menu.add("Güncelle").addActionListener(e -> {

            int selectModelId = this.getTableSelectedRow(this.tbl_model, 0);
            ModelView modelView = new ModelView(this.modelManager.getById(selectModelId));
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                    loadCarTable();
                    loadBookTable(null);
                }
            });
        });
        this.model_menu.add("Sil").addActionListener(e -> {

            if (Helper.confirm("sure")){
                int selectModelId = this.getTableSelectedRow(this.tbl_model, 0);
                if (this.modelManager.delete(selectModelId)) {
                    Helper.showMsg("done");
                    loadModelTable(null);
                    loadCarTable();
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_model.setComponentPopupMenu(model_menu);

        this.btn_search_model.addActionListener(e ->{
            ComboItem selectedBrand = (ComboItem) this.cmb_s_model_brand.getSelectedItem();
            int brandId = 0;
            if (selectedBrand != null){
                brandId = selectedBrand.getKey();
            }
            ArrayList<Model> modelListBySearch = this.modelManager.searchForTable(
                    brandId,
                    (Model.Fuel) this.cmb_s_model_fuel.getSelectedItem(),
                    (Model.Gear) this.cmb_s_model_gear.getSelectedItem(),
                    (Model.Type) this.cmb_s_model_type.getSelectedItem());


            ArrayList<Object[]> modelRowListBySearch = this.modelManager.getForTable(this.col_model.length, modelListBySearch);
            loadModelTable(modelRowListBySearch);
        });

        this.btn_cncl_model.addActionListener(e -> {
            this.cmb_s_model_type.setSelectedItem(null);
            this.cmb_s_model_gear.setSelectedItem(null);
            this.cmb_s_model_fuel.setSelectedItem(null);
            this.cmb_s_model_brand.setSelectedItem(null);
            loadModelTable(null);
        });
    }
        public void loadModelTable(ArrayList<Object[]> modelList){
        this.col_model = new Object[]{"Model ID", "Marka", "Model Adı", "Model Tipi", "Model Yılı", "Yakıt Tipi", "Vites Tipi"};
        if (modelList == null){
            modelList= this.modelManager.getForTable(this.col_model.length, this.modelManager.findAll());
        }
        createTable(this.tmdl_model, this.tbl_model, col_model, modelList);
    }
        public void loadModelFilter(){
        this.cmb_s_model_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_s_model_type.setSelectedItem(null);
        this.cmb_s_model_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_s_model_gear.setSelectedItem(null);

        this.cmb_s_model_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_s_model_fuel.setSelectedItem(null);
        loadModelFilterBrand();

    }
        public void loadModelFilterBrand(){
        this.cmb_s_model_brand.removeAllItems();
        for (Brand obj : brandManager.findAll()){
            this.cmb_s_model_brand.addItem(new ComboItem(obj.getId(), obj.getName()));
        }
        this.cmb_s_model_brand.setSelectedItem(null);
    }

        public void loadBrandComponent(){
        tableRowSelect(this.tbl_brand);

        this.brand_menu = new JPopupMenu();
        this.brand_menu.add("Yeni").addActionListener(e -> {
            BrandView brandView = new BrandView(null);
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();

                }
            });
        });

        this.brand_menu.add("Güncelle").addActionListener(e -> {
            int selectBrandId = this.getTableSelectedRow(this.tbl_brand, 0);
            BrandView brandView = new BrandView(this.brandManager.getById(selectBrandId));
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                    loadBookTable(null);
                }
            });
        });
        this.brand_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")){
                int selectBrandId = this.getTableSelectedRow(this.tbl_brand, 0);
                if (this.brandManager.delete(selectBrandId)) {
                    Helper.showMsg("done");
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                }else{
                    Helper.showMsg("error");
                }
            }
         });
        this.tbl_brand.setComponentPopupMenu(brand_menu);

    }
        public void loadBrandTable(){
        Object[] col_brand = {"Marka ID", "Marka Adı"};
        ArrayList<Object[]> brandList= this.brandManager.getForTable(col_brand.length);
        this.createTable(this.tmdl_brand, this.tbl_brand, col_brand, brandList);
    }

        private void createUIComponents() throws ParseException {
        this.fld_strt_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_strt_date.setText("01/01/2021");
        this.fld_fnsh_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_fnsh_date.setText("10/01/2021");

        this.fld_book_strt_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_book_strt_date.setText("01/01/2021");
        this.fld_book_fnsh_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_book_fnsh_date.setText("10/01/2021");


    }
}
