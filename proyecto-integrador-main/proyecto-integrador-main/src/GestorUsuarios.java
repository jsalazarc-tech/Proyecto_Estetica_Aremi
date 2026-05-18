import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {
    private List<Usuario> usuarios;

    public GestorUsuarios() {
        usuarios = new ArrayList<>();
        // Añadir usuarios de ejemplo
        usuarios.add(new Usuario("admin", "administrador"));
        usuarios.add(new Usuario("user1", "usuario"));
    }

    public Usuario autenticar(String nombre) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null; // Usuario no encontrado
    }
}

