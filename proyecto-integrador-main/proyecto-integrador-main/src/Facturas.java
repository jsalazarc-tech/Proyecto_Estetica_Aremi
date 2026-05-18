import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Facturas {
    // Modificamos la URL para incluir parámetros adicionales
    private static final String DB_URL = "jdbc:mysql://localhost:3306/salon_aremi?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin1023";

    private Connection conexion;

    public Facturas() {
        conectar();
        crearTablaFacturasSiNoExiste();
    }

    public void conectar() {
        try {
            // Cargamos explícitamente el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (conexion != null) {
                System.out.println("Conexión exitosa a la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el driver de MySQL: " + e.getMessage());
        }
    }

    private void crearTablaFacturasSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS facturas (" +
                "id_factura VARCHAR(20) PRIMARY KEY," +
                "usuario VARCHAR(100)," +
                "telefono VARCHAR(20)," +
                "valor_venta DECIMAL(10,2)," +
                "nombre_vendedor VARCHAR(100)," +
                "fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
            // Insertamos algunos datos de prueba si la tabla está vacía
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM facturas");
            if (rs.next() && rs.getInt(1) == 0) {
                String insertData = "INSERT INTO facturas (id_factura, usuario, telefono, valor_venta, nombre_vendedor) " +
                        "VALUES ('1', 'Juan Pérez', '3001234567', 150000, 'María Rodríguez')," +
                        "('2', 'Ana López', '3109876543', 200000, 'María Rodríguez')";
                stmt.execute(insertData);
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla facturas: " + e.getMessage());
        }
    }

    public List<String> buscarFacturasPorNombreOTelefono(String criterio) {
        List<String> facturas = new ArrayList<>();
        String sql = "SELECT id_factura FROM facturas WHERE usuario LIKE ? OR telefono LIKE ?";
        try (PreparedStatement preparar = conexion.prepareStatement(sql)) {
            String parametro = "%" + criterio + "%";
            preparar.setString(1, parametro);
            preparar.setString(2, parametro);

            try (ResultSet resultado = preparar.executeQuery()) {
                while (resultado.next()) {
                    facturas.add(resultado.getString("id_factura"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar facturas: " + e.getMessage());
        }
        return facturas;
    }

    public void generarFactura(String factura) {
        try {
            String usuario = obtenerNombreUsuario(factura);
            String valorVenta = obtenerValorVenta(factura);
            String nombreVendedor = obtenerNombreVendedor(factura);
            String telefono = obtenerTelefono(factura);

            if (usuario == null || valorVenta == null || nombreVendedor == null) {
                System.out.println("No se pudo obtener la información completa de la factura.");
                return;
            }

            Document documento = new Document();
            String nombreArchivo = "Factura_" + factura + ".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            documento.add(new Paragraph("FACTURA DE COMPRA", fontTitle));
            documento.add(new Paragraph("Nombre del Comprador: " + usuario));
            documento.add(new Paragraph("Teléfono: " + (telefono != null ? telefono : "No disponible")));
            documento.add(new Paragraph("Valor de la venta: " + valorVenta));
            documento.add(new Paragraph("Nombre del Vendedor: " + nombreVendedor));

            String rutaImagen = "C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\AREMI.png";
            Image imagenLogo = Image.getInstance(rutaImagen);
            imagenLogo.scaleToFit(100, 100);
            imagenLogo.setAlignment(Image.ALIGN_RIGHT);
            documento.add(imagenLogo);

            documento.close();
            System.out.println("La factura se ha generado y guardado en " + nombreArchivo);
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar el certificado: " + e.getMessage());
        }
    }

    private String obtenerNombreUsuario(String factura) {
        return obtenerCampo(factura, "usuario");
    }

    private String obtenerTelefono(String factura) {
        return obtenerCampo(factura, "telefono");
    }

    private String obtenerValorVenta(String factura) {
        return obtenerCampo(factura, "valor_venta");
    }

    private String obtenerNombreVendedor(String factura) {
        return obtenerCampo(factura, "nombre_vendedor");
    }

    private String obtenerCampo(String factura, String nombreCampo) {
        String valor = null;
        String sql = "SELECT " + nombreCampo + " FROM facturas WHERE id_factura=?";
        try (PreparedStatement preparar = conexion.prepareStatement(sql)) {
            preparar.setString(1, factura);
            try (ResultSet resultado = preparar.executeQuery()) {
                if (resultado.next()) {
                    valor = resultado.getString(nombreCampo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener " + nombreCampo + ": " + e.getMessage());
        }
        return valor;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Facturas factura1 = new Facturas();

        System.out.println("Ingrese el nombre o teléfono del cliente para buscar sus facturas:");
        String criterio = scanner.nextLine();

        List<String> facturasEncontradas = factura1.buscarFacturasPorNombreOTelefono(criterio);

        if (facturasEncontradas.isEmpty()) {
            System.out.println("No se encontraron facturas para el criterio proporcionado.");
            return;
        }

        System.out.println("Facturas encontradas:");
        for (int i = 0; i < facturasEncontradas.size(); i++) {
            System.out.println((i + 1) + ". Factura ID: " + facturasEncontradas.get(i));
        }

        System.out.println("\nIngrese el número de la factura que desea generar (1-" + facturasEncontradas.size() + "):");
        int seleccion = scanner.nextInt();

        if (seleccion >= 1 && seleccion <= facturasEncontradas.size()) {
            factura1.generarFactura(facturasEncontradas.get(seleccion - 1));
        } else {
            System.out.println("Selección inválida.");
        }

        scanner.close();
    }
}