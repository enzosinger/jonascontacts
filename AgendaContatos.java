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

    private Contato contatoEditado;

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
        tipoContatoComboBox = new JComboBox<>(new String[]{"Empresarial", "Amigo", "Cliente", "Estabelecimento", "Familiar", "Serviço", "Emergencial"});
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
        if (nome.isEmpty()) {
            try {
                throw new Excecao("O nome do contato não pode estar vazio.");
            } catch (Excecao e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String telefone = telefoneField.getText();
        String apelido = apelidoField.getText();
        String tipoContato = tipoContatoComboBox.getSelectedItem().toString();

        Contato novoContato;
        switch (tipoContato) {
            case "Empresarial":
                novoContato = new ContatoEmpresarial(nome, telefone, apelido);
                break;
            case "Amigo":
                novoContato = new ContatoAmigo(nome, telefone, apelido);
                break;
            case "Cliente":
                novoContato = new ContatoCliente(nome, telefone, apelido);
                break;
            case "Estabelecimento":
                novoContato = new ContatoEstabelecimento(nome, telefone, apelido);
                break;
            case "Familiar":
                novoContato = new ContatoFamiliar(nome, telefone, apelido);
                break;
            case "Serviço":
                novoContato = new ContatoServico(nome, telefone, apelido);
                break;
            case "Emergencial":
                novoContato = new ContatoEmergencial(nome, telefone, apelido);
                break;
            default:
                novoContato = new Contato(nome, telefone, apelido, tipoContato);
                break;
        }

        contatos.add(novoContato);
        salvarContatos();

        JOptionPane.showMessageDialog(this, "A operação foi realizada com sucesso.");

        nomeField.setText("");
        telefoneField.setText("");
        apelidoField.setText("");
        tipoContatoComboBox.setSelectedIndex(0);

        exibirListaContatos(); // Atualiza a lista de contatos após adicionar um novo contato
    }

    private void exibirListaContatos() {
        contatosPanel.removeAll();

        for (Contato contato : contatos) {
            JPanel contatoPanel = new JPanel(new BorderLayout());
            contatoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel nomeLabel = new JLabel(contato.getNome() + " | ");
            JLabel tipoContatoLabel = new JLabel(contato.getTipoContato());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton editarButton = new JButton("Editar");
            editarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editarContato(contato);
                }
            });

            JButton infoButton = new JButton("Informações do contato");
            infoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exibirInformacoesContato(contato);
                }
            });

            JButton deletarButton = new JButton("Deletar");
            deletarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deletarContato(contato);
                }
            });

            buttonPanel.add(editarButton);
            buttonPanel.add(infoButton);
            buttonPanel.add(deletarButton);

            contatoPanel.add(nomeLabel, BorderLayout.WEST);
            contatoPanel.add(tipoContatoLabel, BorderLayout.CENTER);
            contatoPanel.add(buttonPanel, BorderLayout.EAST);

            contatosPanel.add(contatoPanel);
        }

        contatosPanel.revalidate();
        contatosPanel.repaint();
    }

    private void exibirInformacoesContato(Contato contato) {
        String mensagem = "Nome: " + contato.getNome() + "\nApelido: " + contato.getApelido() + "\nTipo de Contato: " + contato.getTipoContato() + "\nTelefone: " + contato.getTelefone();
        JOptionPane.showMessageDialog(this, mensagem, "Informações do Contato", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarContato(Contato contato) {
        contatoEditado = contato;

        nomeField.setText(contato.getNome());
        telefoneField.setText(contato.getTelefone());
        apelidoField.setText(contato.getApelido());
        tipoContatoComboBox.setSelectedItem(contato.getTipoContato());

        JOptionPane.showMessageDialog(this, "Edição em andamento. Preencha os campos e clique em 'Salvar'.");

        adicionarButton.setText("Salvar");
        adicionarButton.removeActionListener(adicionarButton.getActionListeners()[0]);
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarEdicaoContato(contatoEditado);
            }
        });
    }

    private void salvarEdicaoContato(Contato contato) {
        String novoNome = nomeField.getText();
        String novoTelefone = telefoneField.getText();
        String novoApelido = apelidoField.getText();
        String novoTipoContato = tipoContatoComboBox.getSelectedItem().toString();

        contato.setNome(novoNome);
        contato.setTelefone(novoTelefone);
        contato.setApelido(novoApelido);
        contato.setTipoContato(novoTipoContato);

        salvarContatos();

        JOptionPane.showMessageDialog(this, "A operação foi realizada com sucesso.");

        nomeField.setText("");
        telefoneField.setText("");
        apelidoField.setText("");
        tipoContatoComboBox.setSelectedIndex(0);
        adicionarButton.setText("Adicionar");
        adicionarButton.removeActionListener(adicionarButton.getActionListeners()[0]);
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarContato();
            }
        });

        exibirListaContatos();
    }

    private void deletarContato(Contato contato) {
        contatos.remove(contato);
        salvarContatos();

        JOptionPane.showMessageDialog(this, "O contato foi removido com sucesso.");

        exibirListaContatos();
    }

    private void salvarContatos() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoContatos));

            for (Contato contato : contatos) {
                writer.write(contato.getNome() + "," + contato.getTelefone() + "," + contato.getApelido() + "," + contato.getTipoContato());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void carregarContatos() {
        if (arquivoContatos.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(arquivoContatos));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] dados = line.split(",");
                    String nome = dados[0];
                    String telefone = dados[1];
                    String apelido = dados[2];
                    String tipoContato = dados[3];

                    Contato contato = new Contato(nome, telefone, apelido, tipoContato);
                    contatos.add(contato);
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
                AgendaContatos agenda = new AgendaContatos();
                agenda.carregarContatos();
                agenda.exibirListaContatos();
                agenda.setVisible(true);
    }
}

class Contato implements Serializable {
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

    // getters e setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getTipoContato() {
        return tipoContato;
    }

    public void setTipoContato(String tipoContato) {
        this.tipoContato = tipoContato;
    }
}

class ContatoEmpresarial extends Contato {
    public ContatoEmpresarial(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Empresarial");
    }
}

class ContatoAmigo extends Contato {
    public ContatoAmigo(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Amigo");
    }
}

class ContatoCliente extends Contato {
    public ContatoCliente(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Cliente");
    }
}

class ContatoEstabelecimento extends Contato {
    public ContatoEstabelecimento(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Estabelecimento");
    }
}

class ContatoFamiliar extends Contato {
    public ContatoFamiliar(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Familiar");
    }
}

class ContatoServico extends Contato {
    public ContatoServico(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Serviço");
    }
}

class ContatoEmergencial extends Contato {
    public ContatoEmergencial(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido, "Emergencial");
    }
}

class Excecao extends Exception {
    public Excecao(String mensagem) {
        super(mensagem);
    }
}


