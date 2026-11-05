package co.edu.uniquindio.poo.empresajfx.viewController;

import co.edu.uniquindio.poo.empresajfx.App;
import co.edu.uniquindio.poo.empresajfx.controller.ClienteController;
import co.edu.uniquindio.poo.empresajfx.model.Cliente;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClienteViewController {

    ClienteController clienteController;
    ObservableList<Cliente> listClientes = FXCollections.observableArrayList();
    Cliente selectedCliente;

    @FXML
    private TextField txtCedula;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private Button btbAgregarCliente;

    @FXML
    private Button btnActualizarCliente;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<Cliente> tblListCliente;

    @FXML
    private TableColumn<Cliente, String> tbcCedula;

    @FXML
    private TableColumn<Cliente, String> tbcNombre;

    @FXML
    private TableColumn<Cliente, String> tbcApellido;

    private App app;

    @FXML
    void onAgregarCliente() {
        agregarCliente();
    }

    @FXML
    void onActualizarCliente() {
        actualizarCliente();
    }

    @FXML
    void onLimpiar() {
        limpiarSeleccion();
    }

    @FXML
    void onEliminar() {
        eliminarCliente();
    }

    @FXML
    void initialize() {
        // La empresa es estática en App, por eso se puede usar aquí
        // (setApp() se llama DESPUÉS de initialize()).
        clienteController = new ClienteController(App.empresa);
        initView();
    }

    private void initView() {
        initDataBinding();
        obtenerClientes();
        tblListCliente.getItems().clear();
        tblListCliente.setItems(listClientes);
        listenerSelection();
    }

    private void initDataBinding() {
        tbcCedula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCedula()));
        tbcNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        tbcApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
    }

    private void obtenerClientes() {
        listClientes.addAll(clienteController.obtenerListaClientes());
    }

    private void listenerSelection() {
        tblListCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCliente = newSelection;
            mostrarInformacionCliente(selectedCliente);
        });
    }

    private void mostrarInformacionCliente(Cliente cliente) {
        if (cliente != null) {
            txtCedula.setText(cliente.getCedula());
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
        }
    }

    private void agregarCliente() {
        if (camposVacios()) {
            mostrarAlerta("Completa cédula, nombre y apellido.");
            return;
        }
        Cliente cliente = buildCliente();
        if (clienteController.crearCliente(cliente)) {
            listClientes.add(cliente);
            limpiarCamposCliente();
        } else {
            mostrarAlerta("Ya existe un cliente con esa cédula.");
        }
    }

    private Cliente buildCliente() {
        return new Cliente(txtCedula.getText().trim(), txtNombre.getText().trim(), txtApellido.getText().trim());
    }

    private void eliminarCliente() {
        if (selectedCliente == null) {
            mostrarAlerta("Selecciona un cliente de la tabla.");
            return;
        }
        if (clienteController.eliminarCliente(selectedCliente.getCedula())) {
            listClientes.remove(selectedCliente);
            limpiarSeleccion();
        }
    }

    private void actualizarCliente() {
        if (selectedCliente == null) {
            mostrarAlerta("Selecciona un cliente de la tabla.");
            return;
        }
        if (camposVacios()) {
            mostrarAlerta("Completa cédula, nombre y apellido.");
            return;
        }
        // Evitar duplicar una cédula que ya pertenece a otro cliente
        String nuevaCedula = txtCedula.getText().trim();
        if (!nuevaCedula.equals(selectedCliente.getCedula()) && App.empresa.verificarCliente(nuevaCedula)) {
            mostrarAlerta("Ya existe otro cliente con esa cédula.");
            return;
        }
        if (clienteController.actualizarCliente(selectedCliente.getCedula(), buildCliente())) {
            // El modelo modifica el mismo objeto que está en la tabla: basta con refrescar.
            tblListCliente.refresh();
            limpiarSeleccion();
        }
    }

    private void limpiarSeleccion() {
        tblListCliente.getSelectionModel().clearSelection();
        limpiarCamposCliente();
    }

    private void limpiarCamposCliente() {
        txtCedula.clear();
        txtNombre.clear();
        txtApellido.clear();
    }

    private boolean camposVacios() {
        return txtCedula.getText().isBlank()
                || txtNombre.getText().isBlank()
                || txtApellido.getText().isBlank();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Gestión de clientes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setApp(App app) {
        this.app = app;
    }
}
