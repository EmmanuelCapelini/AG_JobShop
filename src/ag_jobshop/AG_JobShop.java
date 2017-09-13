/*
 Classe principal com a execução do algoritmo genético.
 Baseado o máximo possível no AG elaborado em R.
 Alterações feitas serão comentadas aqui.
 Uma das alterações é para que o código seja orientado a objetos, portanto todos 
 os métodos estão em outras classes, com a exceção do Algoritmo Genético propriamente
 dito, que está nessa classe, e por ter diversos parâmetros, é necessário ser instanciado.
 
 */
package ag_jobshop;

/**
 *
 * @author emmanuel
 */

//IMPORTS DAS BIBLIOTECAS E PACOTES NECESSARIOS
//para ler o arquivo csv do dataset:
import java.io.FileReader; 
import com.opencsv.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import ag_jobshop.Fitness;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import java.lang.Math;
import static java.lang.Math.exp;
import java.util.ArrayList;


public class AG_JobShop {

    

    /**
     * @param args the command line arguments
     */
    
    //Variáveis de controle e etc (Os quais podem ser alterados pela interface grafica)
    private int tam_populacao = 200; //tamanho da populacao dos indivíduos do AG
    private int tx_crossover = 80; //probabilidade de crossover entre os membros da populacao
    private int tx_elitismo = 15; //porcentagem da populacao reservada para efetuar elitismo
    private int tx_mutacao = 3; //probabilidade de um membro da populacao sofrer mutacao
    private int flag_tipoPopulacao = 0; //Tipo de heurística a gerar uma população no AG.
    private int flag_tipomutacao = 3; // Tipo de mutação a ser realizado. 0 para tradiciona, 1 para SA, 2 para GRASP, 3 para VNS e 4 para Load Balance
    private int flag_mutacaoSecundaria = 0; //Tipo de segunda mutação a ser realizada no indivíduo. 0 para nenhuma, 1 a 4 seguem a mutação primária.
    private String penalidade = "999"; //penalidade para tarefas que nao deveriam ser alocadas
    private final CSVReader leitorDataset; //Leitor de arquivos CSV
    private String[][] dataset; //Arquivo do dataset
    private int tam_Dataset_Linhas; // Quantidade de linhas do dataset 
    private Random randomizador;
    private int execucaoAtual = 0;
    private String tipoPopulacao;
    private String tipoMutacao;
    private String mutacaoSecundaria;
    JTextArea saida;
    
    public static void main(String[] args) {
        try {
            /*Método principal do AG, para fins de teste e debug.
            Posteriormente será chamado por interfaces gráficas
            Argumentos chamados por linha de comando:
            tamanhodesejadopop = tamanho da população
            tipogeracaopop = tipo de geração de indivíduos
            tipomutacao = tipo de mutação primária
            tipomutacaosecundaria = segunda mutação, se assim desejado
            execucao = execução do ag na rodada de testes.
            */
            JTextArea dummy = new JTextArea();
            int tamanhoDesejadoPop = Integer.parseInt(args[0]);
            int tipoGeracaoPop = Integer.parseInt(args[1]);
            int tipoMutacao = Integer.parseInt(args[2]);
            int tipoMutacaoSecundaria = Integer.parseInt(args[3]);
            int execucao = Integer.parseInt(args[4]);
            new AG_JobShop(tamanhoDesejadoPop,tipoGeracaoPop,tipoMutacao,tipoMutacaoSecundaria, execucao).algoritmoGenetico(dummy);
            
        } catch (IOException ex) {
            Logger.getLogger(AG_JobShop.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public AG_JobShop(int tamanhoDesejadoPop, int tipoGeracaoPop, int tipoMutacao, int tipoMutacaoSecundaria, int execucao) throws FileNotFoundException, IOException {
        //Esse construtor inicializa tudo que for referente ao AG, faz a leitura do dataset em csv e passa para uma matriz de Strings.
        //Portanto, é necessário fazer o ParseInt ou ParseFloat para manipular os valores em outras funções
        this.leitorDataset = new CSVReader(new FileReader(new File("amendoim2.csv")));
        List<String[]> temp = leitorDataset.readAll();
        dataset = new String[temp.size()][];
        dataset = temp.toArray(dataset);
        for(int i=0;i<dataset.length;i++)
            dataset[i] = dataset[i][0].split(";");
        tam_Dataset_Linhas = dataset.length-1; //Atribui-se o tamanho do dataset como quantidade de linhas. Lembrando que é -1, pois uma linha são as letras
        randomizador = new Random();
        this.flag_tipoPopulacao = tipoGeracaoPop;
        this.tam_populacao = tamanhoDesejadoPop;
        this.flag_tipomutacao = tipoMutacao;
        this.flag_mutacaoSecundaria = tipoMutacaoSecundaria;
        this.execucaoAtual = execucao;
        switch(flag_tipomutacao)
        {
            case 0:
            {
                this.tipoMutacao = "Tradicional";
                break;
            }
            case 1:
            {
                this.tipoMutacao = "SA";
                break;
            }
            case 2:
            {
                this.tipoMutacao = "VNS";
                break;
            }
            case 3:
            {
                this.tipoMutacao = "GRASP";
                break;
            }
            case 4:
            {
                this.tipoMutacao = "LB";
                break;
            }
        }
        switch(flag_mutacaoSecundaria)
        {
            case 0:
            {
                this.mutacaoSecundaria = " Puro";
                break;
            }
            case 1:
            {
                this.mutacaoSecundaria = "+SA";
                break;
            }
            case 2:
            {
                this.mutacaoSecundaria = "+VNS";
                break;
            }
            case 3:
            {
                this.mutacaoSecundaria = "+GRASP";
                break;
            }
            case 4:
            {
                this.mutacaoSecundaria = "+LB";
                break;
            }
        }
        switch(flag_tipoPopulacao)
        {
            case 0:
            {
                this.tipoPopulacao = "Aleatoria";
                break;
            }
            case 1:
            {
                this.tipoPopulacao = "SA";
                break;
            }
            case 2:
            {
                this.tipoPopulacao = "VNS";
                break;
            }
            case 3:
            {
                this.tipoPopulacao = "GRASP";
                break;
            }
            case 4:
            {
                this.tipoPopulacao = "LB";
                break;
            }
        }
    }
    
        public AG_JobShop() throws FileNotFoundException, IOException {
        //Esse construtor inicializa tudo que for referente ao AG, faz a leitura do dataset em csv e passa para uma matriz de Strings.
        //Portanto, é necessário fazer o ParseInt ou ParseFloat para manipular os valores em outras funções
        //Versão sobrecarregada do construtor anterior.
        this.leitorDataset = new CSVReader(new FileReader(new File("amendoim2.csv")));
        List<String[]> temp = leitorDataset.readAll();
        dataset = new String[temp.size()][];
        dataset = temp.toArray(dataset);
        for(int i=0;i<dataset.length;i++)
            dataset[i] = dataset[i][0].split(";");
        tam_Dataset_Linhas = dataset.length-1; //Atribui-se o tamanho do dataset como quantidade de linhas. Lembrando que é -1, pois uma linha são as letras
        randomizador = new Random();
        
    }
    
    /**
     *
     * @param iteracoes
     * @param pctElite
     * @param populacaoAnterior
     * @param tamanhopop
     * @return
     */
    
    public void algoritmoGenetico(JTextArea ta) throws IOException{
       /*
       Nessa função será executado o Algoritmo Genético.
       Por fins de organização, será utilizada essa função e não a Main, para que
       o objeto possa ser instanciado os parâmetros de controle ṕossam ser passados.
       */
       int iteracao = 1;
       double melhor = Double.MAX_VALUE;
       double melhorAnterior = 99999;
       int converge = 1;
       int x = 0;
       int indicemelhor = 0;
       saida = ta;
       /*A seguinte linha estava dentro de um if que é executado apenas uma vez
       no código em R. Por ora, fica fora do laço porque o efeito talvez seja o mesmo. */
       char[][] novaPopulacao = null;
       char[] melhorIndividuo = null;
       //Outra variaçao para o código:
       //char[][] populacao = criaPopAleatoria(x,tx_elitismo,null,tam_populacao);
       //Enquanto não houverem 10 gerações consecutivas sem melhora, o algoritmo é executado
       int porcentagemElite = (tx_elitismo/100) * tam_populacao;
       int posInicial = porcentagemElite + 1;
       /*As seguintes linhas decraram e configuram o objeto File e FileWriter necessários
       para a saída em txt do programa. */
       //String nomeArquivo = "Log_MutacaoTipo"+flag_tipomutacao+"Exec"+System.currentTimeMillis()+".txt" ;
       String nomeArquivo = "Log_PopulacaoTipo"+tipoPopulacao+"Tam"+tam_populacao+"Mutacao "+tipoMutacao+mutacaoSecundaria+"Exec_"+execucaoAtual+".txt" ;
       File log = new File(nomeArquivo);
       log.createNewFile();
       FileWriter ag_logger = new FileWriter(log);
       /*Uma saída simples para o console, replicada no txt*/
       ag_logger.write("Algoritmo genético iniciado, populacao tipo "+tipoPopulacao+ " mutacao tipo " +tipoMutacao+mutacaoSecundaria+"\n");
       System.out.println("Algoritmo genético iniciado, populacao tipo "+tipoPopulacao+ " mutacao tipo " +tipoMutacao+mutacaoSecundaria);
       
       
       /*A partir daqui, este laço se refere ao Algoritmo Genético. A condicao de parada é : 10 gerações estagnadas.*/
       while(converge !=50){
           double media = 0;
           x +=1;
           char[][] sequenciamento = null; //Instancia a referência para o Sequenciamento.7
           /*Dependendo do tipo de mutação setado no flag, a população é instanciada de uma forma diferente.*/
           switch(flag_tipoPopulacao)
           {
               case 0:
               {
                   sequenciamento = Populacao.criaPopAleatoria(iteracao, tx_elitismo, novaPopulacao, tam_populacao, tam_Dataset_Linhas, dataset, penalidade);
                   break;
               }
               case 1:
               {
                   sequenciamento = Populacao.criaPopSA(iteracao, tx_elitismo, novaPopulacao, tam_populacao, tam_Dataset_Linhas, dataset, penalidade);
                   break;
               }
               
               case 2:
               {
                   sequenciamento = Populacao.criaPopVNS(iteracao, tx_elitismo, novaPopulacao, tam_populacao, tam_Dataset_Linhas, dataset, penalidade);
                   break;
               }
               
               case 3:
               {
                   sequenciamento = Populacao.criaPopGRASP(iteracao, tx_elitismo, novaPopulacao, tam_populacao, tam_Dataset_Linhas, dataset, penalidade);
                   break;
               }
               
           }
           
           //Declara um vetor de objetos Fitness e uma nova população para receber o Elitismo.
           Fitness[] valorFit = new Fitness[tam_populacao];
           novaPopulacao = new char[tam_populacao][tam_Dataset_Linhas];
           //Avalia os indivíduos (cada posição no vetor corresponde a uma linha na população)
           for(int i = 0;i<valorFit.length;i++)
           {
               valorFit[i] = new Fitness();
               valorFit[i].setPosicao(i);
               valorFit[i].setFitness(Fitness.calculaFitness(sequenciamento[i].clone(), dataset));
           }
           valorFit = Populacao.elitismo(valorFit); //NOTA: Talvez seja melhor colocar o reordenador dentro de outra classe sem o nome de elitismo. 
           //Após o array estar ordenado, são inseridos os melhores indivíduos na novapopulação.
           for(int i=0;i<=porcentagemElite;i++)
           {
              novaPopulacao[i] = sequenciamento[valorFit[i].getPosicao()].clone();
           }
           
           for(int i = posInicial;i<tam_populacao;i++)
           {
               if(randomizador.nextInt(101)<tx_crossover)
               {
                   //Se o número aleatório está dentro da porcentagem de crossover, a posição é preenchida por crossover de indivíduos
                   //São selecionados os indivíduos para fazer crossover, por meio de torneio, para garantir aleatoriedade.
                   int pai1, pai2, r1, r2;
                   r1 = randomizador.nextInt(tam_populacao);
                   r2 = randomizador.nextInt(tam_populacao);
                   if(Fitness.calculaFitness(sequenciamento[r1].clone(), dataset)>Fitness.calculaFitness(sequenciamento[r2].clone(), dataset))
                       pai1 = r2;
                   else
                       pai1 = r1;
                   r1 = randomizador.nextInt(tam_populacao);
                   r2 = randomizador.nextInt(tam_populacao);
                   if(Fitness.calculaFitness(sequenciamento[r1].clone(), dataset)>Fitness.calculaFitness(sequenciamento[r2].clone(), dataset))
                       pai2 = r2;
                   else
                       pai2 = r1;
                   novaPopulacao[i] = Populacao.crossover(sequenciamento[pai1].clone(),sequenciamento[pai2].clone(),tam_Dataset_Linhas);
                }
               else{
                   //Se não está, recebe um indivíduo aleatório
                   for(int sequencia = 0; sequencia<tam_Dataset_Linhas;sequencia++)
                   {
                        //Escolhe aleatoriamente uma máquina dentre as possíveis para esse job
                        char temp = MetodosAuxiliares.amostra(dataset[sequencia+1].clone(),penalidade);
                        //Insere a mesma em sua respectiva coluna na população.
                        novaPopulacao[i][sequencia] = temp;
                    }
               }
           }
           //Após gerada a nova população, é percorrido de novo para realizar as mutações
           //O tipo de mutação dependerá do que for selecionado no combo Box.
           for(int i = posInicial;i<tam_populacao;i++)
           {
               if(randomizador.nextInt(101)<tx_mutacao)
               {
                   switch(flag_tipomutacao)
                   {
                       case 0:
                       {
                           novaPopulacao[i] = Mutacao.mutacao(novaPopulacao[i], tam_Dataset_Linhas, dataset, penalidade);
                           break;
                       }
                       case 1:
                       {
                           novaPopulacao[i] = SimulatedAnnealing.mutacaoSA(novaPopulacao[i], dataset, tam_Dataset_Linhas, penalidade);
                           break;
                       }
                       case 2:
                       {
                           novaPopulacao[i] = VNS.mutacaoVNS(novaPopulacao[i], tam_Dataset_Linhas, dataset, penalidade);
                           break;
                       }
                       case 3:
                       {
                           novaPopulacao[i] = GRASP.mutacaoGRASP(novaPopulacao[i], tam_Dataset_Linhas, dataset, penalidade);
                           break;
                       }
                       case 4:
                       {
                           novaPopulacao[i] = LoadBalancing.mutacaoLoadBalancing(novaPopulacao[i], dataset, penalidade);
                           break;
                       }
                       default:
                           novaPopulacao[i] = Mutacao.mutacao(novaPopulacao[i], tam_Dataset_Linhas, dataset, penalidade);
                   }
                   //Se foi definida uma mutação secundária, ela é realizada nesse trecho de código.
                   if(flag_mutacaoSecundaria>0)
                   {
                        switch(flag_mutacaoSecundaria)
                   {
                       case 1:
                       {
                           novaPopulacao[i] = SimulatedAnnealing.mutacaoSA(novaPopulacao[i], dataset, tam_Dataset_Linhas, penalidade);
                           break;
                       }
                       case 2:
                       {
                           novaPopulacao[i] = VNS.mutacaoVNS(novaPopulacao[i], tam_Dataset_Linhas, dataset, penalidade);
                           break;
                       }
                       case 3:
                       {
                           novaPopulacao[i] = GRASP.mutacaoGRASP(novaPopulacao[i], tam_Dataset_Linhas, dataset, penalidade);
                           break;
                       }
                       case 4:
                       {
                           novaPopulacao[i] = LoadBalancing.mutacaoLoadBalancing(novaPopulacao[i], dataset, penalidade);
                           break;
                       }
                    }
                   }
               }
               
               /*
               nota importante: no algoritmo original, se o indivíduo não estiver dentro
               da probabilidade de mutação, ele recebe o seu equivalente em string (sequenciamento, nesse código)
               Porém, na minha concepção, isso basicamente desperdiçaria o crossover recém efetuado e portanto
               não estou fazendo isso nesse código. De qualquer forma, se for necessário, basta descomentar a linha:
               */
               //else
               //   novaPopulacao[i] = sequenciamento[i];
           }
           //A nova população é reavaliada para fins de elitismo
           for(int i = 0;i<valorFit.length;i++)
           {
               valorFit[i] = new Fitness();
               valorFit[i].setPosicao(i);
               valorFit[i].setFitness(Fitness.calculaFitness(novaPopulacao[i].clone(), dataset));
           }
           Populacao.elitismo(valorFit);
           for(int i=0;i<=porcentagemElite;i++)
           {
              //Uma diferença desse trecho para o anterior de elitismo é que é feito um 'swap' entre os indivíduos ao invés de simplesmente alocar.
              // Ou seja, os melhores indivíduos são jogados para as primeiras posições, tendo seu lugar trocado com quem estaria originalmente lá.
              char[] temp = novaPopulacao[i].clone();
              novaPopulacao[i] = novaPopulacao[valorFit[i].getPosicao()].clone();
              novaPopulacao[valorFit[i].getPosicao()] = temp;
           }
           //Não possui nada a ver com o algoritmo, apenas encontra os maiores e menores fitnesses
           for(Fitness f:valorFit)
           {
               if(f.getFitness()<melhor)
               {
                   melhor = f.getFitness();
                   indicemelhor = f.getPosicao();
               }
               media += f.getFitness();
           }
           media = media/tam_populacao;
           System.out.println("Iteracao "+iteracao+" Melhor Fit: "+melhor+" Media de Fitnesses: "+media);
           saida.append("Iteracao "+iteracao+" Melhor Fit: "+melhor+" Media de Fitnesses: "+media+"\n");
           iteracao++;
           if(melhor<melhorAnterior)
           {
               melhorAnterior = melhor;
               converge = 1;
           }
           else
               converge++;
       }
       melhorIndividuo = novaPopulacao[indicemelhor].clone();
       System.out.println(" Fit do melhor individuo: " +melhor);
       saida.append(" Fit do melhor individuo: " +melhor);
       ag_logger.write("\nFit do melhor indivíduo: "+melhorAnterior+"\n");
       System.out.println("Melhor individuo:");
       saida.append("\nMelhor individuo:");
       for(char c:melhorIndividuo)
       {
           System.out.print(" "+c);
           saida.append(" "+c);
       }
       ag_logger.close();
   }
   

   
   public int posicaoLetra(char letra){
       //Essa função simplesmente retorna a posição no vetor referente à letra passada por parâmetro.
       int posicaoletra = 0;
       char[] maquinas = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'};
       for(int i=0;i<maquinas.length;i++){
           if(letra==maquinas[i])
               posicaoletra = i;
       }
       return posicaoletra;
   }
   
   //==========================A PARTIR DAQUI, OS SETTERS E GETTERS QUANDO NECESSÁRIOS ==================================

    /**
     * @param tam_populacao the tam_populacao to set
     */
    public void setTam_populacao(int tam_populacao) {
        this.tam_populacao = tam_populacao;
    }

    /**
     * @param tx_crossover the tx_crossover to set
     */
    public void setTx_crossover(int tx_crossover) {
        this.tx_crossover = tx_crossover;
    }

    /**
     * @param tx_elitismo the tx_elitismo to set
     */
    public void setTx_elitismo(int tx_elitismo) {
        this.tx_elitismo = tx_elitismo;
    }

    /**
     * @param tx_mutacao the tx_mutacao to set
     */
    public void setTx_mutacao(int tx_mutacao) {
        this.tx_mutacao = tx_mutacao;
    }

    /**
     * @param penalidade the penalidade to set
     */
    public void setPenalidade(String penalidade) {
        this.penalidade = penalidade;
    }
    
    /**
     * @param flag_tipomutacao the flag_tipomutacao to set
     */
    public void setFlag_tipomutacao(int flag_tipomutacao) {
        this.flag_tipomutacao = flag_tipomutacao;
    }
}