package fr.loria.score;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author: Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
@RunWith (Suite.class)
@Suite.SuiteClasses({
        CausalOrderTest.class,
        CommunicationServiceTest.class,
        MultipleEditingSessionsTest.class
})
public class AllTests {}
