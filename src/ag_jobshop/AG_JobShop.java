/*
 Classe principal com a execução do algoritmo genético.
 Baseado o máximo possível no AG elaborado em R.
 Alterações feitas serão comentadas aqui.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import java.lang.Math;
import static java.lang.Math.exp;


public class AG_JobShop {

    

    /**
     * @param args the command line arguments
     */
    
    //Variáveis de controle e etc (Os quais podem ser alterados pela interface grafica)
    private int tam_populacao = 200; //tamanho da populacao dos indivíduos do AG
    private int tx_crossover = 40; //probabilidade de crossover entre os membros da populacao
    private int tx_elitismo = 15; //porcentagem da populacao reservada para efetuar elitismo
    private int tx_mutacao = 30; //probabilidade de um membro da populacao sofrer mutacao
    private int flag_tipomutacao = 2; // Tipo de mutação a ser realizado. 0 para tradiciona, 1 para SA, 2 para GRASP e 3 para VNS
    private String penalidade = "999"; //penalidade para tarefas que nao deveriam ser alocadas
    private final CSVReader leitorDataset; //Leitor de arquivos CSV
    private String[][] dataset; //Arquivo do dataset
    private int tam_Dataset_Linhas; // Quantidade de linhas do dataset 
    private Random randomizador;
    JTextArea saida;
    
    public static void main(String[] args) {
        try {
            /*Método principal do AG, para fins de teste e debug.
            Posteriormente será chamado por interfaces gráficas*/
            JTextArea dummy = new JTextArea();
            new AG_JobShop().algoritmoGenetico(dummy);
        } catch (IOException ex) {
            Logger.getLogger(AG_JobShop.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public AG_JobShop() throws FileNotFoundException, IOException {
        //Esse construtor faz a leitura do dataset em csv e passa para uma matriz de Strings.
        //Portanto, é necessário fazer o ParseInt ou ParseFloat para manipular os valores em outras funções
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
    public char[][] criaPopAleatoria(int iteracoes, int pctElite, char [][] populacaoAnterior,
                                int tamanhopop){
        /* Função para alocação e criação de uma população.
        Argumentos:
        iteracoes: quantidade de iterações já ocorridas do argumento.
        pctElite: quantidade de membros da população que se referem à porcentagem dentro do elitismo
        populacaoAnterior: população da iteração anterior do AG, para fins de elitismo
        tamanhopop: tamanho da população
        */
        
        //Instancia a população a ser retornada.Até o presente momento, o melhor modo de se representar a população foi com uma matriz de chars.
        char[][] populacao = new char[tamanhopop][tam_Dataset_Linhas];
        int posInicial;
//        for(int i = 0; i < 10; i++){
//            for(int j = 0; j < 10; j++)
//                populacao[i][j] = new Agendamento();
//        }
        if(iteracoes != 1)
        {
            //Efetua o elitismo caso essa não seja a primeira execução da função
            for(int i=0;i<=pctElite;i++)
                populacao[i] = populacaoAnterior[i].clone();
            posInicial = pctElite + 1;
        }
        else
            posInicial = 0;
        
        //Aqui inicia-se o alocamento dos indivíduos na nova população
        for(int i = posInicial;i<tamanhopop;i++)
        {
            for(int sequencia = 0; sequencia<tam_Dataset_Linhas;sequencia++)
            {
                //Escolhe aleatoriamente uma máquina dentre as possíveis para esse job
                char temp = amostra(dataset[sequencia+1].clone());
                //Insere a mesma em sua respectiva coluna na população.
                populacao[i][sequencia] = temp;
            }
        }
        return populacao;
    }
    
    public char[] crossover(char[] pai1,char[] pai2){
        /*
        Função que realiza o crossover de dois indivíduos, no caso, duas linhas da Matriz População
        Nessa primeira tentativa, o crossover é realizado de forma comum, Uma vez que o crossover não
        altera a ordem de agendamento das tarefas, então não se faz necessário um crossover de permutação
        */
        int pontoCross = randomizador.nextInt(tam_Dataset_Linhas);
        char[] filho = pai1;
//        for(int i=0;i<pontoCross;i++)
//            filho[i] = pai1[i];
        for(int i =pontoCross;i<pai2.length;i++)
            filho[i] = pai2[i];
        return filho;
    }
    
   public char[] mutacao(char[] individuoOriginal){
       /*
       Função que realiza a mutação de uma linha da população
       A mutação funciona selecionando um gene qualquer do indivíduo e substituindo
       a máquina contida nele por uma outra constando em sua respectiva linha no dataset.
       */
       char[] individuoMutado = individuoOriginal.clone();
       //AS SEGUINTES LINHAS PODEM CONTER ERROS PARA PERCORRER O VETOR E MATRIZ
       int pontoMutacao = randomizador.nextInt(tam_Dataset_Linhas - 1) +1;
       char geneMutado = amostra(dataset[pontoMutacao]);
       individuoMutado[pontoMutacao-1] = geneMutado;
       return individuoMutado;
   }
   
   private char[] mutacaoSA(char[] individuoOriginal){
       /*
       Realiza uma mutação em um indivíduo utilizando o princípio do Simulated Annealing.
       A "Temperatura" se inicia em 80 sempre e o fator de resfriamento posteriormente
       será definido pelo usuário.
       */
       float temperatura = 80;
       float fatorResfriamento = 0.3f;
       do{
           char[] aleatorio = unicoIndividuoAleatorio();
           double fitOriginal = fitness(individuoOriginal,tam_populacao);
           double fitNovo = fitness(aleatorio,tam_populacao);
           if(fitNovo<fitOriginal)
               individuoOriginal = aleatorio.clone();
           else if((int)randomizador.nextInt(100)<temperatura)
               individuoOriginal = aleatorio.clone();
           temperatura = temperatura * fatorResfriamento;
       }while(temperatura>5);
       return individuoOriginal;
   }
   
   private char[] mutacaoSAComplexa(char[] individuoOriginal){
       /*
       Realiza uma mutação em um indivíduo utilizando o princípio do Simulated Annealing.
       A "Temperatura" se inicia em 80 sempre e o fator de resfriamento posteriormente
       será definido pelo usuário. É uma "forma diferenciada" de se contar iterações. 
       Nesta versão do SA, o cálculo da probabilidade é feito usando o exponencial.
       */
       int temperatura = 80;
       int fatorResfriamento = 2;
       //Esse loop roda enquanto o sistema estiver "quente"
       do{
           char[] aleatorio = unicoIndividuoAleatorio();
           double fitOriginal = fitness(individuoOriginal,tam_populacao);
           double fitNovo = fitness(aleatorio,tam_populacao);
           if(fitNovo<fitOriginal)
               individuoOriginal = aleatorio.clone();
           else if(exp((fitNovo-fitOriginal)/temperatura)<temperatura)
               individuoOriginal = aleatorio.clone();
           temperatura = temperatura - fatorResfriamento;
       }while(temperatura>5);
       return individuoOriginal;
   }
   
   private char[] mutacaoVNSv1(char[] individuoOriginal){
       /*
       Realiza uma mutação em um indivíduo usando o princípio da VNS: Variable Neighborhood Search.
       Por ora o tamanho de vizinhaças exploradas é fixo, mas poderá ser alterado para 
       o usuário setar.
       Como entende-se por "vizinhança" qualquer alteração a um indivíduo, ela 
       é composta nessa versão por um conjunto de mutações simples ao indivíduo original.
       */
       int tamanhoVizinhanca = 50;
       char[][] neighborhood = new char[tamanhoVizinhanca][];
       //As "tamanhoVizinhanca" linhas da vizinhança são instanciadas com uma 
       //mutação simples do indivíduo em cada uma. Problema: podem ocorrer repetições.
       for(int i = 0;i<tamanhoVizinhanca;i++)
       {
           neighborhood[i] = mutacao(individuoOriginal).clone();
       }
       //Aqui são instanciadas variáveis de controle para a seleção do melhor indivíduo da VNS
       double melhorFit = Double.MAX_VALUE;
       int posicaoMelhorVizinho = 0;
       for(int i = 0; i< tamanhoVizinhanca; i++)
       {
           double fitTemporario = fitness(neighborhood[i].clone(),tam_populacao);
           if(fitTemporario<melhorFit)
           {
               melhorFit = fitTemporario;
               posicaoMelhorVizinho = i;
           }
       }
       return neighborhood[posicaoMelhorVizinho].clone();
   }
   
   private char[] mutacaoVNS(char[] individuoOriginal){
       /*
       Realiza uma mutação em um indivíduo usando o princípio da VNS: Variable Neighborhood Search.
       Por ora o tamanho de vizinhaças exploradas é fixo, mas poderá ser alterado para 
       o usuário setar.
       Como entende-se por "vizinhança" qualquer alteração a um indivíduo, ela 
       é composta nessa versão por um conjunto de mutações simples ao indivíduo original.
       Nesta versão da VNS, são feitas iterações sucessivas até que uma estagnação
       da melhora (quando houver) ocorra.
       */
       int tamanhoVizinhanca = 50;
       char[][] neighborhood = new char[tamanhoVizinhanca][];
       char[] melhorAtual = individuoOriginal.clone();
       //Aqui são instanciadas variáveis de controle para a seleção do melhor indivíduo da VNS
       double melhorFit = Double.MAX_VALUE;
       int posicaoMelhorVizinho = 0;
       int iteracoesEstagnadas = 0;
       do{
           //As "tamanhoVizinhanca" linhas da vizinhança são instanciadas com uma 
           //mutação simples do indivíduo em cada uma. Problema: podem ocorrer repetições.
           for(int i = 0;i<tamanhoVizinhanca;i++)
           {
                neighborhood[i] = mutacao(melhorAtual).clone();
           }
           for(int i = 0; i< tamanhoVizinhanca; i++)
           {
                double fitTemporario = fitness(neighborhood[i].clone(),tam_populacao);
                if(fitTemporario<melhorFit)
                {
                    melhorFit = fitTemporario;
                    posicaoMelhorVizinho = i;
                }
           }
           if(melhorFit>fitness(melhorAtual,tam_populacao))
           {
               melhorAtual = neighborhood[posicaoMelhorVizinho].clone();
               iteracoesEstagnadas = 0;
           }
           else
               iteracoesEstagnadas++;
       }while(iteracoesEstagnadas<=5);
       return melhorAtual;
   }
   
   public double fitness(char[] individuoAvaliado,int tamanhoPop){
       /*
       Essa função calcula o Makespam do individuo.
       Ela é feita percorrendo todo o caminho fornecido pelo vetor na matriz e 
       somando-se os valores.
       Pode ter ficado pesado. Caso isso seja verdade, seria melhor encontrar
       uma alternativa para o data frame do R.
       */
       float valor =0;
       int linha = 0;
       //char[] maquinas = {'A','B','C','D','E','F','G','H','I','J','K','L','M','O','P','Q','R','S'};
       double[] fit = new double[dataset[0].length];
       /*Por padrão os vetores já são inicializados com zeros. Mas se ocorrer algum
       problema com lixo de memória, descomentar esse trecho.
       //Inicia fit com zeros
       for(int i=0;i<individuoAvaliado.length;i++)
       {
           fit[i] = 0; 
       }
       */
       for(int i=0;i<individuoAvaliado.length;i++)
       {
           //Na posição do vetor referente à letra, soma-se o valor correspondente à mesma no dataset. 
           fit[posicaoLetra(individuoAvaliado[i])] += Double.parseDouble(dataset[i+1][posicaoLetra(individuoAvaliado[i])]);
       }
       //Feito isso, calcula-se o maior dos tempos. Esse será o fitness.
       double piorFitness = Double.MIN_VALUE;
       for(int i=0;i<fit.length;i++){
           if(fit[i]>piorFitness)
               piorFitness = fit[i];
       }
       return piorFitness;
   }
   
   public void algoritmoGenetico(JTextArea ta){
       /*
       Nessa função será executado o Algoritmo Genético.
       Por fins de organização, será utilizada essa função e não a Main, para que
       o objeto possa ser instanciado e as funções serem chamadas.
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
       System.out.println("Algoritmo genético iniciado, mutacao tipo" +flag_tipomutacao);
       while(converge !=10){
           double media = 0;
           x +=1;
           char[][] sequenciamento = criaPopAleatoria(x,porcentagemElite,novaPopulacao,tam_populacao);
           //Esse é usando um vetor de doubles. O usado atualmente é um vetor de objetos fitness
           //double[] valorFit = new double[tam_populacao];
           Fitness[] valorFit = new Fitness[tam_populacao];
           novaPopulacao = new char[tam_populacao][tam_Dataset_Linhas];
           //Avalia os indivíduos (cada posição no vetor corresponde a uma linha na população)
           for(int i = 0;i<valorFit.length;i++)
           {
               valorFit[i] = new Fitness();
               valorFit[i].setPosicao(i);
               valorFit[i].setFitness(fitness(sequenciamento[i].clone(),tam_populacao));
           }
           /*
           As próximas linhas efetuam o elitismo na população, ordenando o vetor de fitnesses
           Isso é feito utilizando o método sort da classe Arrays, pricipalmente para fins de 
           organização de código. Se houver impacto na performance, será implementado um sort do zero.
           Também foi necessário sobrecarregar o comparador.
           */
           Arrays.sort(valorFit, (Fitness f1, Fitness f2) -> {
                if (f1.getFitness() > f1.getFitness()) return 1;
                if (f1.getFitness() < f2.getFitness()) return -1;
                return 0;
                });
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
                   if(fitness(sequenciamento[r1].clone(),tam_populacao)>fitness(sequenciamento[r2].clone(),tam_populacao))
                       pai1 = r2;
                   else
                       pai1 = r1;
                   r1 = randomizador.nextInt(tam_populacao);
                   r2 = randomizador.nextInt(tam_populacao);
                   if(fitness(sequenciamento[r1].clone(),tam_populacao)>fitness(sequenciamento[r2].clone(),tam_populacao))
                       pai2 = r2;
                   else
                       pai2 = r1;
                   novaPopulacao[i] = crossover(sequenciamento[pai1].clone(),sequenciamento[pai2].clone());
                }
               else{
                   //Se não está, recebe um indivíduo aleatório
                   for(int sequencia = 0; sequencia<tam_Dataset_Linhas;sequencia++)
                   {
                        //Escolhe aleatoriamente uma máquina dentre as possíveis para esse job
                        char temp = amostra(dataset[sequencia+1].clone());
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
                           novaPopulacao[i] = mutacao(novaPopulacao[i]);
                           break;
                       }
                       case 1:
                       {
                           novaPopulacao[i] = mutacaoSAComplexa(novaPopulacao[i]);
                           break;
                       }
                       case 2:
                       {
                           novaPopulacao[i] = mutacaoVNS(novaPopulacao[i]);
                           break;
                       }
                       default:
                           novaPopulacao[i] = mutacao(novaPopulacao[i]);
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
               valorFit[i].setFitness(fitness(novaPopulacao[i].clone(),tam_populacao));
           }
           Arrays.sort(valorFit, (Fitness f1, Fitness f2) -> {
                if (f1.getFitness() > f1.getFitness()) return 1;
                if (f1.getFitness() < f2.getFitness()) return -1;
                return 0;
                });
           for(int i=0;i<=porcentagemElite;i++)
           {
              //Uma diferença desse trecho para o anterior de elitismo é que é feito um 'swap' entre os indivíduos ao invés de simplesmente alocar.
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
       System.out.println("Melhor individuo:");
       saida.append("Melhor individuo:");
       for(char c:melhorIndividuo)
       {
           System.out.print(" "+c);
           saida.append(" "+c);
       }
   }
   
   public char[] unicoIndividuoAleatorio(){
       /*Essa função instancia e devolve um único indivíduo feito aleatoriamente*/
       char[] novoTruta = new char[tam_Dataset_Linhas];
       for(int sequencia = 0; sequencia<tam_Dataset_Linhas;sequencia++)
          {
          //Escolhe aleatoriamente uma máquina dentre as possíveis para esse job
          char temp = amostra(dataset[sequencia+1].clone());
          //Insere a mesma em sua respectiva coluna na população.
          novoTruta[sequencia] = temp;
          }
       return novoTruta;
   }
   
   public String[] reordenaVetor(String []vetOriginal){
       /*
       Essa função reordena um vetor usando os métodos da classe Collections.
       Era usada para os vetores de agendamento mas pode não ser mais necessária
       */
       String[] vetNovo = null;
       //Verifica se o valor não está dentro da penalidade
       for(int i =0; i<vetOriginal.length; i++)
       {
           if(vetOriginal[i].compareTo(penalidade)!=0)
               vetNovo[i] = vetOriginal[i];               
       }
       //Após isso reordena aleatoriamente 
       List<String> temp = Arrays.asList(vetNovo);
       Collections.shuffle(temp);
       //temp.toArray(vetNovo);
       vetNovo = temp.toArray(vetNovo);
       return vetNovo;
   }
   
   public char amostra(String[] linha){
       /*Gera um número aleatório referente a uma posição na linha do dataset.
       Se posição não tiver penalidade, é obtida a coluna referente e retornada
       Se a posição tiver penalidade, tenta novamente.*/
       char amostra = 'Z';
       int numeroAleatorio;
       int tamLinha = linha.length;
       char[] maquinas = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'};
       while(amostra=='Z'){
           numeroAleatorio = randomizador.nextInt(tamLinha);
           if(linha[numeroAleatorio].compareTo(penalidade)!=0)
               amostra = maquinas[numeroAleatorio];
       }
       return amostra;
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