/**
 * 2. 某电商业务场景2：
 * 运营推广部门某次策划上线秒杀或者优惠活动，经测试人员估算压力测试，大约在一个小时内进来100万+用户访问，系统吞吐量固定的情况下，为保障Java服务端正常运行不崩溃，需要对正常访问用户进行限流处理，大约每秒响应1000个请求。
 * 请问限流的系统如何设计，给出具体的实现？（服务端框架采用spring boot+mybatis+redis
 */

/**
 * 令牌桶
 * 后续可使用redis-cell令牌桶支持分布式
 */
public class LimitTest {

    /**
     * 每秒处理数（放入令牌数量）
     */
    private long putTokenRate;

    /**
     * 最后刷新时间
     */
    private long refreshTime;

    /**
     * 令牌桶容量
     */
    private long capacity;

    /**
     * 当前桶内令牌数
     */
    private long currentToken = 0L;

    /**
     * 令牌桶桶算法
     * @return
     */
    boolean tokenBucketTryAcquire() {

        long currentTime = System.currentTimeMillis();
        //生成的令牌 =(当前时间-上次刷新时间)* 放入令牌的速率
        long generateToken = (currentTime - refreshTime) / 1000 * putTokenRate;
        // 当前令牌数量 = 之前的桶内令牌数量+放入的令牌数量
        currentToken = Math.min(capacity, generateToken + currentToken);
        // 刷新时间
        refreshTime = currentTime;

        //桶里面还有令牌，请求正常处理
        if (currentToken > 0) {
            currentToken--; //令牌数量-1
            return true;
        }

        return false;
    }

}
