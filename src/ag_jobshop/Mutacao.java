/*
Essa classe implementa uma mutação simples para o AG em questão.
O método mutacao realiza a mutação (maior parte de seus argumentos se faz necessária
por não ter como a classe estática instanciá-los) 
O método amostra() é uma alternativa à função sample() do R, e é chamado pelo 
próprio método mutacao, não tendo nenhuma outra utilidade e por isso é privado.
*/
package ag_jobshop;

import java.util.Random;

/**
 *
 * @author emmanuel
 */
public class Mutacao {
    
    public static char[] mutacao(char[] individuoOriginal, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
       /*
       Função que realiza a mutação de uma linha da população
       A mutação funciona selecionando um gene qualquer do indivíduo e substituindo
       a máquina contida nele por uma outra constando em sua respectiva linha no dataset.
       */
       Random randomizador = new Random();
       char[] individuoMutado = individuoOriginal.clone();
       //AS SEGUINTES LINHAS PODEM CONTER ERROS PARA PERCORRER O VETOR E MATRIZ
       int pontoMutacao = randomizador.nextInt(tam_Dataset_Linhas - 1) +1;
       char geneMutado = amostra(dataset[pontoMutacao],penalidade);
       individuoMutado[pontoMutacao-1] = geneMutado;
       return individuoMutado;
   }
    
   private static char amostra(String[] linha, String penalidade){
       /*Gera um número aleatório referente a uma posição na linha do dataset.
       Se posição não tiver penalidade, é obtida a coluna referente e retornada
       Se a posição tiver penalidade, tenta novamente.*/
       Random randomizador = new Random();
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
}
