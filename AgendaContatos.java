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
        exibirListaContatos();
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
                throw new IllegalArgumentException("Tipo de contato inválido: " + tipoContato);
        }

        contatos.add(novoContato);
        limparCampos();
        atualizarArquivoContatos();
        exibirListaContatos();
    }

    private void limparCampos() {
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

            JLabel nomeLabel = new JLabel(contato.getNome() + " | " + contato.getTipoContato());
            JLabel tipoContatoLabel = new JLabel(contato.getTipoContato());
            contatoPanel.add(tipoContatoLabel, BorderLayout.CENTER);
            contatoPanel.add(nomeLabel, BorderLayout.CENTER);

            JButton informacoesButton = new JButton("Informações");
            informacoesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exibirInformacoesContato(contato);
                }
            });

            JButton editarButton = new JButton("Editar");
            editarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editarContato(contato);
                }
            });

            JButton removerButton = new JButton("Remover");
            removerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removerContato(contato);
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(informacoesButton);
            buttonPanel.add(editarButton);
            buttonPanel.add(removerButton);
            contatoPanel.add(buttonPanel, BorderLayout.EAST);
            contatosPanel.add(contatoPanel);
        }

        contatosPanel.revalidate();
        contatosPanel.repaint();
    }

    private void exibirInformacoesContato(Contato contato) {
        String mensagem = "Nome: " + contato.getNome() + "\n" +
                "Telefone: " + contato.getTelefone() + "\n" +
                "Apelido: " + contato.getApelido() + "\n" +
                "Tipo de Contato: " + contato.getTipoContato();

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

        // Atualizar o tipo de contato se for diferente
        if (!contato.getTipoContato().equals(novoTipoContato)) {
            Contato novoContato;
            switch (novoTipoContato) {
                case "Empresarial":
                    novoContato = new ContatoEmpresarial(novoNome, novoTelefone, novoApelido);
                    break;
                case "Amigo":
                    novoContato = new ContatoAmigo(novoNome, novoTelefone, novoApelido);
                    break;
                case "Cliente":
                    novoContato = new ContatoCliente(novoNome, novoTelefone, novoApelido);
                    break;
                case "Estabelecimento":
                    novoContato = new ContatoEstabelecimento(novoNome, novoTelefone, novoApelido);
                    break;
                case "Familiar":
                    novoContato = new ContatoFamiliar(novoNome, novoTelefone, novoApelido);
                    break;
                case "Serviço":
                    novoContato = new ContatoServico(novoNome, novoTelefone, novoApelido);
                    break;
                case "Emergencial":
                    novoContato = new ContatoEmergencial(novoNome, novoTelefone, novoApelido);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de contato inválido: " + novoTipoContato);
            }
            contatos.remove(contato);
            contatos.add(novoContato);
        }

        salvarContatos();

        JOptionPane.showMessageDialog(this, "A operação foi realizada com sucesso.");

        limparCampos();
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


    private void salvarContatos() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoContatos));

            for (Contato contato : contatos) {
                writer.write(contato.getNome() + ";" + contato.getTelefone() + ";" + contato.getApelido() + ";" + contato.getTipoContato());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void removerContato(Contato contato) {
        int resposta = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover o contato?", "Confirmação de Remoção", JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            contatos.remove(contato);
            atualizarArquivoContatos();
            exibirListaContatos();
        }
    }

    private void atualizarArquivoContatos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoContatos))) {
            for (Contato contato : contatos) {
                writer.write(contato.getNome() + ";" + contato.getTelefone() + ";" + contato.getApelido() + ";" + contato.getTipoContato());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar contatos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void carregarContatos() {
        if (arquivoContatos.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoContatos))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 4) {
                        String nome = parts[0];
                        String telefone = parts[1];
                        String apelido = parts[2];
                        String tipoContato = parts[3];

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
                                throw new IllegalArgumentException("Tipo de contato inválido: " + tipoContato);
                        }

                        contatos.add(novoContato);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar contatos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AgendaContatos agenda = new AgendaContatos();
                agenda.carregarContatos();
                agenda.setVisible(true);
                agenda.exibirListaContatos();
            }
        });
    }
}

// Classe Contato (abstrata)
abstract class Contato {
    private String nome;
    private String telefone;
    private String apelido;

    public Contato(String nome, String telefone, String apelido) {
        this.nome = nome;
        this.telefone = telefone;
        this.apelido = apelido;
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

    public abstract String getTipoContato();

    public void setNome(String novoNome) {
        this.nome = novoNome;
    }

    public void setTelefone(String novoTelefone) {
        this.telefone = novoTelefone;
    }

    public void setApelido(String novoApelido) {
        this.apelido = novoApelido;
    }

    public void setTipoContato(String novoTipoContato) {
    }
}

// Subclasses de Contato
class ContatoEmpresarial extends Contato {
    public ContatoEmpresarial(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Empresarial";
    }
}

class ContatoAmigo extends Contato {
    public ContatoAmigo(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Amigo";
    }
}

class ContatoCliente extends Contato {
    public ContatoCliente(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Cliente";
    }
}

class ContatoEstabelecimento extends Contato {
    public ContatoEstabelecimento(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Estabelecimento";
    }
}

class ContatoFamiliar extends Contato {
    public ContatoFamiliar(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Familiar";
    }
}

class ContatoServico extends Contato {
    public ContatoServico(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Serviço";
    }
}

class ContatoEmergencial extends Contato {
    public ContatoEmergencial(String nome, String telefone, String apelido) {
        super(nome, telefone, apelido);
    }

    @Override
    public String getTipoContato() {
        return "Emergencial";
    }
}

class Excecao extends Exception {
    public Excecao(String mensagem) {
        super(mensagem);
    }
}
