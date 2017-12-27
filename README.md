Algoritmo Genético implementando em Java para meu TCC. Neste arquivo Readme é feita uma rápida documentação do projeto.
Versão do Readme: 1.0 (12/2017)
1. Execução:
	Interface gráfica ainda não está totalmente implementada, a execução do algoritmo em sua totalidade é possível apenas via linha de comandos.
	Para executar, usar o comando em linha de comando, ou usar os argumentos na IDE: 
		java -jar AGJobshop tamanhopopulação tipodepopulação tipodemutaçãoprimaria tipodemutaçãosecundaria 
	Onde os argumentos significam:
		tamanhopopulação: Um número inteiro apontando para tamanho da população desejado para a execução; 
		tipodepopulação: O tipo de geração de população, também um número inteiro de 0 para aleatória, 1 para Grasp, 2 para Simulated Annealing e 3 para VNS; 
		tipodemutaçãoprimaria: A mutação primária, sendo 0 para tradicional, 1 para GRASP, 2 para Simulated Annealing, 3 para VNS e 4 para Load Balancing 
		tipodemutaçãosecundaria: A segunda mutação, executada imediatamente após a primeira, também um número inteiro, sendo 0 para nenhuma (mutação principal pura), 1 para GRASP, 2 para Simulated Annealing, 3 para VNS e 4 para Load Balancing.
2. Edição: 
	O projeto foi implementado totalmente no IDE Netbeans, utilizando as bibliotecas apontadas na pasta lib. Recomendo ler a licença Apache 2.0 sob a qual o opencsv se encontra.
	No presente momento, todas as demais bibliotecas foram deletadas da pasta, ficando apenas o opencsv, único necessário para o funcionamento do projeto.
3. Funcionalidades e classes:
	O programa teve as funcionalidades espalhadas ao longe de suas classes, ficando da seguinte forma:
		AG_Jobshop.java: Rotina principal do algoritmo genético.
		Fitness.java: Cálculo do Fitness
		GRASP.java: Operações referentes à heurística Greedy Randomized Adaptive Search Procedure
		LoadBalancing.java: Operações referentes à heurística Load Balancing.
		MetodosAuxiliares.java: Métodos auxiliares implementados para suprir a falta de alguns originais da linguagem R.
		Mutacao.java: Operações referentes à mutação do Algoritmo Genético.
		Populacao.java: Operações referentes à manipulações de Populações geradas pelo algoritmo genético tradicional.
		SimulatedAnnealing.java: Operações referentes à heurística Simulated Annealing
		VNS.java: Operações referentes à heurística VNS
	Todas as classes possuem comentários, quando possível e necessário, no cabeçalho do arquivo, explicando de forma mais clara o funcionamento das heurísticas e métodos.
	