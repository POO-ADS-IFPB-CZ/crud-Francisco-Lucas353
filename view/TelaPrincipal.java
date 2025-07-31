package view;

import dao.ProdutoDao;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.Set;

public class TelaPrincipal extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;
    private ProdutoDao dao;

    public TelaPrincipal() {
        super("CRUD de Produtos");

        try {
            dao = new ProdutoDao();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar DAO: " + e.getMessage());
            System.exit(1);
        }

        // Tabela
        modelo = new DefaultTableModel(new Object[]{"Código", "Descrição", "Preço"}, 0);
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);

        // Botões
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnRemover = new JButton("Remover");

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRemover);

        // Layout
        add(scroll, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações
        btnAdicionar.addActionListener(e -> adicionar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnRemover.addActionListener(e -> remover());

        listarProdutos();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void listarProdutos() {
        try {
            modelo.setRowCount(0); // limpa
            Set<Produto> produtos = dao.getAll();
            for (Produto p : produtos) {
                modelo.addRow(new Object[]{p.getCodigo(), p.getDescricao(), p.getPreco()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao listar: " + e.getMessage());
        }
    }

    private void adicionar() {
        try {
            String codStr = JOptionPane.showInputDialog(this, "Código:");
            if (codStr == null) return;
            int codigo = Integer.parseInt(codStr);

            String descricao = JOptionPane.showInputDialog(this, "Descrição:");
            if (descricao == null) return;

            String precoStr = JOptionPane.showInputDialog(this, "Preço:");
            if (precoStr == null) return;
            double preco = Double.parseDouble(precoStr);

            Produto p = new Produto(codigo, descricao, preco);
            if (dao.salvar(p)) {
                JOptionPane.showMessageDialog(this, "Produto salvo!");
                listarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "Produto já existe.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar: " + e.getMessage());
        }
    }

    private void atualizar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para atualizar.");
            return;
        }

        try {
            int codigo = (int) modelo.getValueAt(linha, 0);

            String novaDescricao = JOptionPane.showInputDialog(this, "Nova descrição:");
            if (novaDescricao == null) return;

            String novoPrecoStr = JOptionPane.showInputDialog(this, "Novo preço:");
            if (novoPrecoStr == null) return;
            double novoPreco = Double.parseDouble(novoPrecoStr);

            Produto atualizado = new Produto(codigo, novaDescricao, novoPreco);
            if (dao.atualizar(atualizado)) {
                JOptionPane.showMessageDialog(this, "Produto atualizado!");
                listarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar produto.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para remover.");
            return;
        }

        int codigo = (int) modelo.getValueAt(linha, 0);
        Produto p = new Produto(codigo, "", 0.0); // dados irrelevantes para equals

        try {
            if (dao.remover(p)) {
                JOptionPane.showMessageDialog(this, "Produto removido.");
                listarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "Produto não encontrado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao remover: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }
}
