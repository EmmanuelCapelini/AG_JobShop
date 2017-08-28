/*
Nessa classe são implementados alguns métodos para auxiliar a manipulação de objetos.
Maior parte destes são métodos análogos a funções do R.
*/
package ag_jobshop;

import java.util.Random;

/**
 *
 * @author emmanuel
 */
public class MetodosAuxiliares {
    public static char amostra(String[] linha, String penalidade){
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
