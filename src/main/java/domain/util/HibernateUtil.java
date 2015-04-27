package domain.util;

//public class HibernateUtil {
//
//    private static final SessionFactory sessionFactory = buildSessionFactory();
//
//    private static SessionFactory buildSessionFactory() {
//        try {
//            Configuration configuration = new Configuration();
//            configuration.configure();
//            StandardServiceRegistryBuilder standardServiceRegistryBuilder =
//                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
//            return configuration.buildSessionFactory(standardServiceRegistryBuilder.build());
//        } catch (Throwable ex) {
//            throw new ExceptionInInitializerError(ex);
//        }
//    }
//
//    public static SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
//}