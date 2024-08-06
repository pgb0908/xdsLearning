# RDS ~ EDS 처리방안
- 각 정보에 대해 계속해서 Listening해야한다.
- main thread에서는 각 정보에 대해 변경이 있으면 notice!

-> 차후, notice 정보를 각 데이터로 관리해야한다, Map?
- 1. main thread : read
- 2. listening thread : write

비동기 처리를 적극 활용하기 위해
completable future 를 조사했었음.

하지만, 생각해보니 설계 상 필요 없을 것으로 보임.
결과물을 return으로 전달하고 새로운 비동기를 올리고 하는 형태가 아니다.

각각 future로 처리하고, 내부에서 send Ack을 future로 다시 열어주는?
    get을 써야한다면, 그 정도의 업무만 필요할 것으로 보인다.

xDS를 받아오는 각각의 future, 스레드들은 그대로 유지하며 계속 reading을 한다.
그리고, 요청이 들어오면 ret 하지 않고, 그 값을 concurrent Map에 담아주면 된다.

그리고, main에서는 각 concurrent Map의 정보를 읽어들이며, 기존 값과 비교해 차이가 있으면 
그 차이를 다시 적용하면 된다.

