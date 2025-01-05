package com.vrsoftware.checkout.transaction_manager;

import com.sun.net.httpserver.HttpServer;
import com.vrsoftware.checkout.transaction_manager.controller.TransactionController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;

@SpringBootApplication
public class TransactionManagerApplication {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(TransactionManagerApplication.class);

        TransactionController transactionController = context.getBean(TransactionController.class);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/transactions", transactionController);

        server.setExecutor(null);
        server.start();

        System.out.println("Servidor HTTP iniciado na porta 8080");
    }
}
