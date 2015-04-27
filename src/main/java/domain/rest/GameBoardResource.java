package domain.rest;

import domain.dto.GameOutput;
import domain.entity.GameBoard;
import domain.entity.Move;
import domain.entity.Player;
import domain.entity.UserSession;
import domain.repository.GameRepository;
import domain.repository.PlayerRepository;
import domain.repository.SessionRepository;
import domain.rest.exceptions.UserException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

public class GameBoardResource {

    SessionRepository sessionRepository;
    GameRepository gameRepository;
    PlayerRepository playerRepository;

    public GameBoardResource(SessionRepository sessionRepository, GameRepository gameRepository,
                             PlayerRepository playerRepository) {
        if (sessionRepository == null)
            throw new IllegalArgumentException("Parameter 'sessionRepository' can't be null");
        if (gameRepository == null)
            throw new IllegalArgumentException("Parameter 'tweetRepository' can't be null");
        if (playerRepository == null)
            throw new IllegalArgumentException("Parameter 'tweetRepository' can't be null");
        this.playerRepository = playerRepository;
        this.sessionRepository = sessionRepository;
        this.gameRepository = gameRepository;
    }
    @GET
    @Path("/user/{login}/games")
    @Produces("application/json")
    public Response allGames (@HeaderParam("token") String token, @PathParam("login") String username) {
        if (!isTokenCorrect(token))
            throw new UserException(Response.Status.BAD_REQUEST,"Token was empty");
        UserSession userSession = sessionRepository.getByToken(token);
        if (userSession == null || userSession.isExpired()) {
            throw new UserException(Response.Status.UNAUTHORIZED, "Your token is invalid");
        }

        List<GameBoard> gameBoards = gameRepository.getAllByLogin(username);
        return Response.status(200).entity(new GameOutput(gameBoards)).build();
    }

    @GET
    @Path("/game/id")
    @Produces("application/json")
    public Response getGame (@HeaderParam("token") String token, @PathParam("id") int gameID) {
        if (!isTokenCorrect(token))
            throw new UserException(Response.Status.BAD_REQUEST,"Token was empty");
        UserSession userSession = sessionRepository.getByToken(token);
        if (userSession == null || userSession.isExpired())
            throw new UserException(Response.Status.UNAUTHORIZED, "Your token is invalid");

        GameBoard game = gameRepository.getByID(gameID);
        if(game == null)
            throw new UserException(Response.Status.BAD_REQUEST,"Game by this id was not found");

        return Response.status(200).entity(game).build();
    }


    //ATTENTION: добавила idPlayer, который сделал ход, если не нужно, перепишите
    //предлагаю вынести проверку хода в отдельный класс
    @POST
    @Path("commitMove/{id}")
    @Produces("application/json")
    public Response setMove (@HeaderParam("token") String token, @PathParam("id") int gameID, List<Move> moves, int playerID) {
        if (!isTokenCorrect(token))
            throw new UserException(Response.Status.BAD_REQUEST,"Token was empty");
        UserSession userSession = sessionRepository.getByToken(token);
        if (userSession == null || userSession.isExpired())
            throw new UserException(Response.Status.UNAUTHORIZED, "Your token is invalid");

        GameBoard game = gameRepository.getByID(gameID);
        if(game == null)
            throw new UserException(Response.Status.BAD_REQUEST,"Game by this id was not found");
        Player currentPlayer = playerRepository.getByID(playerID);
        if(currentPlayer == null)
            throw new UserException(Response.Status.BAD_REQUEST,"Player by this id was not found");
        //TODO здесь необходимо прописать логику проверки ходов (в moves)  и изменить состояние gameboard, player, руки
        //if все плохо
        //throw new UserException(Response.Status.BAD_REQUEST,"Moves are invalid");
        //if все хорошо
        //return Response.status(200).entity(здесь стоит отправить класс, который надо написать
        // "содержит игрока и кто ходит").build();
        return null;
    }

    @POST
    @Path ("/changeHand/{id}")
    @Produces("application/json")
    public Response changeHand (@HeaderParam("token") String token, @PathParam("id") int gameID,
                                int playerID, List<Integer> idFigures) {
        if (!isTokenCorrect(token))
            throw new UserException(Response.Status.BAD_REQUEST,"Token was empty");
        UserSession userSession = sessionRepository.getByToken(token);
        if (userSession == null || userSession.isExpired())
            throw new UserException(Response.Status.UNAUTHORIZED, "Your token is invalid");
        GameBoard game = gameRepository.getByID(gameID);
        if(game == null)
            throw new UserException(Response.Status.BAD_REQUEST,"Game by this id was not found");
        Player currentPlayer = playerRepository.getByID(playerID);
        if(currentPlayer == null)
            throw new UserException(Response.Status.BAD_REQUEST,"Player by this id was not found");
        //TODO change hand of current player
        //return Response.status(200).entity(currentPlayer).build();
        return null;
    }





    private static boolean isTokenCorrect(String token){
        return !(token == null || token.trim().isEmpty());
    }
}
