# Entrega 3 SD - Relatório

## Número de replicas

Nesta implementação, o sistema opera com 3 réplicas (A, B e C). Cada uma delas corresponde à primeira, segunda e terceira entradas num timestamp, respectivamente.

## Escritas

Optámos por fazer com que a réplica responda a uma operação de escrita assim que possível, indicando apenas se a operação foi aceite (ou seja, inserida no update log/ledger da réplica) ou não. Dessa forma, o utilizador só será notificado de um erro durante uma escrita se o servidor estiver indisponível. Se o utilizador desejar verificar se o update ocorreu, ele pode fazer um pedido de leitura (ou seja, balance).

Para melhorar a eficiência, os updates recebidos numa réplica são armazenados numa priority queue, que utiliza um comparador baseado no prevTS. Isso evita a necessidade de percorrer a totalidade da ledger sempre que precisamos obter os updates que podem tornar-se estáveis. Como consequência, deixa de ser necessário o atributo adicional a indicar se o update se encontra estável.
## Leituras

Na leitura, a nossa abordagem consiste em deixar o pedido pendente até que a condição causal seja satisfeita, momento em que a réplica responde ao utilizador.

## Gossip

Ao receber um pedido de gossip de um administrador, a réplica propaga a mensagem gossip para todos os servidores activos. Por questões de eficiência, ao enviar uma mensagem gossip, também enviamos a replicaTS do remetente. Isso permite que, ao criar uma mensagem gossip, a réplica remetente obtenha estimativas dos updates que as outras réplicas ainda não possuem, reduzindo assim o tamanho do estado enviado.

Desta forma, garantimos uma comunicação eficiente entre as réplicas, optimizando o processo de sincronização e mantendo o sistema actualizado e em conformidade com as condições causais estabelecidas.