import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class AgendaContatos extends JFrame {
    private JTextField nomeField, telefoneField, apelidoField;
    private JComboBox<String> tipoContatoComboBox;
    private JButton adicionarButton, listaButton;
    private JPanel contatosPanel;

    private ArrayList<Contato> contatos;
    private File arquivoContatos;

    public AgendaContatos() {
        contatos = new ArrayList<>();
        arquivoContatos = new File("contatos.txt");

        setTitle("Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de cadastro de contato
        JPanel cadastroPanel = new JPanel(new GridLayout(6, 2));
        cadastroPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField();
        JLabel telefoneLabel = new JLabel("Telefone:");
        telefoneField = new JTextField();
        JLabel apelidoLabel = new JLabel("Apelido:");
        apelidoField = new JTextField();
        JLabel tipoContatoLabel = new JLabel("Tipo de Contato:");
        tipoContatoComboBox = new JComboBox<>(new String[]{"Empresarial", "Parente", "Amigo"});
        adicionarButton = new JButton("Adicionar");
        listaButton = new JButton("Lista de Contatos");

        cadastroPanel.add(nomeLabel);
        cadastroPanel.add(nomeField);
        cadastroPanel.add(telefoneLabel);
        cadastroPanel.add(telefoneField);
        cadastroPanel.add(apelidoLabel);
        cadastroPanel.add(apelidoField);
        cadastroPanel.add(tipoContatoLabel);
        cadastroPanel.add(tipoContatoComboBox);
        cadastroPanel.add(new JLabel());
        cadastroPanel.add(adicionarButton);
        cadastroPanel.add(new JLabel());
        cadastroPanel.add(listaButton);

        // Painel de lista de contatos
        contatosPanel = new JPanel();
        contatosPanel.setLayout(new BoxLayout(contatosPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contatosPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(cadastroPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarContato();
            }
        });

        listaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirListaContatos();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void adicionarContato() {
        String nome = nomeField.getText();
        String telefone = telefoneField.getText();
        String apelido = apelidoField.getText();
        String tipoContato = tipoContatoComboBox.getSelectedItem().toString();

        Contato novoContato = new Contato(nome, telefone, apelido, tipoContato);
        contatos.add(novoContato);
        salvarContatos();

        JOptionPane.showMessageDialog(this, "A operação foi realizada com sucesso.");

        nomeField.setText("");
        telefoneField.setText("");
        apelidoField.setText("");
        tipoContatoComboBox.setSelectedIndex(0);
    }

    private void exibirListaContatos() {
        contatosPanel.removeAll();

        for (Contato contato : contatos) {
            JPanel contatoPanel = new JPanel(new BorderLayout());
            contatoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel nomeLabel = new JLabel(contato.getNome());
            JLabel tipoContatoLabel = new JLabel(contato.getTipoContato());

            JButton editarButton = new JButton("Editar");
            editarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editarContato(contato);
                }
            });

            JButton deletarButton = new JButton("Deletar");
            deletarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deletarContato(contato);
                }
            });

            contatoPanel.add(nomeLabel, BorderLayout.WEST);
            contatoPanel.add(tipoContatoLabel, BorderLayout.CENTER);
            contatoPanel.add(editarButton, BorderLayout.EAST);
            contatoPanel.add(deletarButton, BorderLayout.SOUTH);

            contatosPanel.add(contatoPanel);
        }

        contatosPanel.revalidate();
        contatosPanel.repaint();
    }

    private void editarContato(Contato contato) {
        nomeField.setText(contato.getNome());
        telefoneField.setText(contato.getTelefone());
        apelidoField.setText(contato.getApelido());
        tipoContatoComboBox.setSelectedItem(contato.getTipoContato());

        int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja editar esse contato?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            contatos.remove(contato);
            salvarContatos();

            JOptionPane.showMessageDialog(this, "Edição realizada com sucesso.");
        }
    }

    private void deletarContato(Contato contato) {
        int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar esse contato?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            contatos.remove(contato);
            salvarContatos();

            JOptionPane.showMessageDialog(this, "A operação foi realizada com sucesso.");
            exibirListaContatos();
        }
    }

    private void salvarContatos() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoContatos));
            for (Contato contato : contatos) {
                writer.write(contato.getNome() + ";" + contato.getTelefone() + ";" +
                        contato.getApelido() + ";" + contato.getTipoContato());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarContatos() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arquivoContatos));
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 4) {
                    String nome = partes[0];
                    String telefone = partes[1];
                    String apelido = partes[2];
                    String tipoContato = partes[3];
                    Contato contato = new Contato(nome, telefone, apelido, tipoContato);
                    contatos.add(contato);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Contato {
        private String nome;
        private String telefone;
        private String apelido;
        private String tipoContato;

        public Contato(String nome, String telefone, String apelido, String tipoContato) {
            this.nome = nome;
            this.telefone = telefone;
            this.apelido = apelido;
            this.tipoContato = tipoContato;
        }

        public String getNome() {
            return nome;
        }

        public String getTelefone() {
            return telefone;
        }

        public String getApelido() {
            return apelido;
        }

        public String getTipoContato() {
            return tipoContato;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AgendaContatos agenda = new AgendaContatos();
                agenda.setVisible(true);
                agenda.carregarContatos();
            }
        });
    }
}