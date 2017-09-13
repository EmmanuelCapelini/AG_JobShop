/*
Nesta classe é implementado o método do Balanceamento de Carga, ou Load Balancing
O funcionamento está discriminado nos métodos.
*/
package ag_jobshop;

/**
 *
 * @author emmanuel
 */
public class LoadBalancing {
    
    public static char[] mutacaoLoadBalancing(char[] individuoOriginal, String[][] dataset, String penalidade) {
        /*Esse método realiza o balanceamento de carga. Ele tenta encontrar uma
        ordem de produção em comum da máquina que representa o gargalo do sistema com uma máquina ociosa
        e o realoca.*/
        char[] individuoBalanceado = individuoOriginal.clone();
        int gargalo = Fitness.calculaGargalo(individuoOriginal, dataset);
        //int ociosa = Fitness.calculaOcio(individuoOriginal, dataset);
        int[] ordemTrocada = ordemPermutavel(gargalo, dataset, penalidade);
        int ordem = ordemTrocada[0];
        int maquina = ordemTrocada[1];
        if(ordem >=0 && maquina >=0)
        {
            individuoBalanceado[ordem] = posicaoLetra(maquina);
        }
        //Se o indivíduo balanceado tiver um makespan maior (logo, Fitness pior) do que o original, é retornado o original
        if(Fitness.calculaFitness(individuoBalanceado, dataset) > Fitness.calculaFitness(individuoOriginal, dataset))
            return individuoOriginal;
        return individuoBalanceado;
    }
    
    private static int[] ordemPermutavel(int gargalo, String[][] dataset, String penalidade){
        /*Esse método tenta encontrar uma máquina para realizar uma troca 
        de alocação de jobs.
        Retorna um vetor de duas posições, sendo a primeira a ordem de produção
        e a segunda a máquina.*/
        
        int posicaoPermutavel[] = {-1,-1};
        for(int ordem = 1; ordem < dataset.length-1; ordem++)
        {
            if(dataset[ordem][gargalo].compareTo(penalidade)!=0)
            {
                double fitGargalo = Double.parseDouble(dataset[ordem][gargalo]);
                for(int maquina = 0; maquina < dataset[ordem].length; maquina++)
                {
                    //Realizar uma verificação para ver se o makespan deste em outra máquina é menor ou igual.
                    //Retornar a LINHA, das 188. Aí substitui-se pela letra equivalente.
                    double possivelMenor = Double.parseDouble(dataset[ordem][maquina]);
                    if(possivelMenor <= fitGargalo && maquina != gargalo)
                    {
                        posicaoPermutavel[0] = ordem;
                        posicaoPermutavel[1] = maquina;
                    }
                }
            }
        }
        return posicaoPermutavel;
    }
    
    private static char posicaoLetra(int posicao){
       //Essa função simplesmente retorna a posição no vetor referente à letra passada por parâmetro.
       char[] maquinas = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'};
       char maquinaApontada = maquinas[posicao];
       return maquinaApontada;
   }
}
