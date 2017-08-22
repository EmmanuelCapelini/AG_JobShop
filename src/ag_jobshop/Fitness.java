/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
