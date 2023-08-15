package com.ssmr.txpractice.propagation;

import com.ssmr.txpractice.SpringMybatisConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMybatisConfig.class)
public class PropagationJunitTest {
    private final Logger log = LoggerFactory.getLogger(PropagationJunitTest.class);

    @Autowired
    private PropagationService propagation;

    @Test
    public void test21(){
        //propagation.testREQUIREDTxUpNotify();
        //propagation.testNESTEDFunction();
        //propagation.wrongWayNESTEDFunction();
        // propagation.hybridSelfInvokeAndOtherRequired();
        propagation.testTwoREQUIRESNEWinNormalMethod();
    }

    @Autowired
    private CompactPropagationService compactPropagation;

    @Test
    public void test31() {
        compactPropagation.test01_AMethod();
    }

}
