
package dao;

import model.Produto;
import java.io.IOException;

public class ProdutoDao extends GenericDao<Produto> {
    public ProdutoDao() throws IOException {
        super("produtos.dat");
    }
}
