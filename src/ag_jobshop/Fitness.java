/*
Essa classe calcula o fitness de um indivíduo.
Ela também pode ser instanciada como um objeto auxiliar que armazena o valor de um
fitness e a posição ao qual o seu dono está na população, para que não seja perdido
em  casos onde a reordenação de um vetor seja necessária.
Maiores informações sobre o funcionamento estão especificados no corpo dos métodos
*/
package ag_jobshop;

/**
 *
 * @author emman
 */

import java.util.ArrayList;
import java.util.List;
public class Fitness {
    
    private int posicao;
    private double fitness;
    
    public Fitness() {
        
    }
    
    public Fitness(double fitInicial){
        fitness = fitInicial;
    }
    
    public static double calculaFitness(char[] individuoAvaliado,String[][] dataset){
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
       
    private static int posicaoLetra(char letra){
       //Essa função simplesmente retorna a posição no vetor referente à letra passada por parâmetro.
       int posicaoletra = 0;
       char[] maquinas = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'};
       for(int i=0;i<maquinas.length;i++){
           if(letra==maquinas[i])
               posicaoletra = i;
       }
       return posicaoletra;
   }
    
    // === Getters e setters para instanciar um objeto Fitness ==== //
    
        /**
     * @return the posicao
     */
    public int getPosicao() {
        return posicao;
    }

    /**
     * @param posicao the posicao to set
     */
    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    /**
     * @return the fitness
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * @param fitness the fitness to set
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
}
