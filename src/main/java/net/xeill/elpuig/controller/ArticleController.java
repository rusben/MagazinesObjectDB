package net.xeill.elpuig.controller;

import net.xeill.elpuig.model.Article;
import net.xeill.elpuig.model.Author;
import net.xeill.elpuig.model.Magazine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class ArticleController {

  private EntityManagerFactory entityManagerFactory;

  private MagazineController magazineController = new MagazineController();
  private AuthorController authorController = new AuthorController();

  public ArticleController() { }

  public ArticleController(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * @param articlesFile Aquest String correspon amb l'arxiu on s'emmagatzemen les
   *                     dades de les isntancies de Revista
   * @return ArrayList d'objectes Revista, amb els seus articles i la
   * informació de l'autor
   * @throws IOException <dt><b>Preconditions:</b>
   *                     <dd>
   *                     filename<>nil </br> llistaRevistes<>nil </br>
   *                     llistaRevistes.getRevista(i).getArticles()== nil</br>
   *                     <dt><b>Postconditions:</b>
   *                     <dd>
   *                     llistaRevistes.getRevistes()<>nil</br>
   *                     llistaRevistes.getRevista(i).getArticles()<>nil</br>
   *                     llistaRevistes.getRevista(i).getArticle(j)<>nil</br>
   *                     llistaRevistes
   *                     .getRevista(i).getArticle(j).getAutor()<>nil</br>
   */
  public List<Magazine> readArticlesFile(String articlesFile, String magazinesFile, String authorsFile)
      throws IOException {
    int articleId, magazineId, authorId;
    String title;
    Date creationDate;
    boolean publishable;
    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");

    BufferedReader br = new BufferedReader(new FileReader(articlesFile));
    String linea = "";

    List<Magazine> magazinesList = magazineController.readMagazinesFile(magazinesFile);
    List<Author> authorList = authorController.readAuthorsFile(authorsFile);

    while ((linea = br.readLine()) != null) {
      StringTokenizer str = new StringTokenizer(linea, ",");
      articleId = Integer.parseInt(str.nextToken());
      magazineId = Integer.parseInt(str.nextToken());
      authorId = Integer.parseInt(str.nextToken());
      title = str.nextToken();

      try {
        creationDate = dateFormat.parse(str.nextToken());
        publishable = Boolean.parseBoolean(str.nextToken());

        magazinesList.get(magazineId - 1).addArticle(new Article(articleId, title, creationDate, publishable, authorList.get(authorId - 1)));

      } catch (ParseException e) {

        e.printStackTrace();
      }

    }
    br.close();

    return magazinesList;
  }

  public List<Article>  readArticlesFile(String articlesFile, String authorsFile) throws IOException {
    int articleId, magazineId, authorId;
    String title;
    Date creationDate;
    boolean publishable;
    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");

    BufferedReader br = new BufferedReader(new FileReader(articlesFile));
    String linea = "";
    List<Author> authorList = authorController.readAuthorsFile(authorsFile);
    List<Article> articlesList = new ArrayList<Article>();

    while ((linea = br.readLine()) != null) {
      StringTokenizer str = new StringTokenizer(linea, ",");
      articleId = Integer.parseInt(str.nextToken());
      magazineId = Integer.parseInt(str.nextToken());
      authorId = Integer.parseInt(str.nextToken());
      title = str.nextToken();

      try {
        creationDate = dateFormat.parse(str.nextToken());
        publishable = Boolean.parseBoolean(str.nextToken());

        articlesList.add(new Article(articleId, title, creationDate, publishable, authorList.get(authorId - 1)));

      } catch (ParseException e) {

        e.printStackTrace();
      }

    }
    br.close();
    return articlesList;

  }

  /* Method to CREATE an Article in the database */
  public void addArticle(Article article) {
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    em.merge(article);
    em.getTransaction().commit();
    em.close();
  }

  /* Method to READ all Articles */
  public void listArticles() {
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    List<Article> result = em.createQuery("from Article", Article.class)
        .getResultList();
    for (Article article : result) {
      System.out.println(article.toString());
    }
    em.getTransaction().commit();
    em.close();
  }

  /* Method to UPDATE activity for an Article */
  public void updateArticle(Integer articleId) {
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Article article = (Article) em.find(Article.class, articleId);
    em.merge(article);
    em.getTransaction().commit();
    em.close();
  }

  /* Method to DELETE an Article from the records */
  public void deleteArticle(Integer articleId) {
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Article article = (Article) em.find(Article.class, articleId);
    em.remove(article);
    em.getTransaction().commit();
    em.close();
  }


}
