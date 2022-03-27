package com.soccer.management.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.TransferDTO;
import com.soccer.management.dto.TransferFilterDTO;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.model.Transfer;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.TransferMapper;
import com.soccer.management.repository.TransferRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.IPlayerService;
import com.soccer.management.service.ITeamService;
import com.soccer.management.service.ITransferService;
import com.soccer.management.service.IUserService;

/**
 * @author enes.boyaci
 */
@Service
public class TransferService implements ITransferService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private IPlayerService playerService;

    @Autowired
    private TransferMapper transferMapper;

    @Autowired
    private ITransferService transferService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITeamService teamService;

    @Autowired
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TransferDTO transferPlayer(TransferDTO transferDTO) {

        boolean isAdmin = jwtUtil.getIsAdminFromToken();

        if (isAdmin) {
            throw new BadRequestException("Admin cannot transfer players!");
        }

        PlayerDTO player = playerService.getById(transferDTO.getPlayerId());

        if (Objects.isNull(player))
            throw new ResourceNotFoundException("Player not found with given id!");

        Transfer transfer = transferRepository.findByPlayerId(player.getId());

        if (Objects.nonNull(transfer))
            throw new BadRequestException("Player is already waiting transfer!");

        User user = userService.getByEmail(jwtUtil.getUsernameFromToken());

        if (Objects.isNull(user))
            throw new BadRequestException("User not found with given team!");

        TeamDTO team = teamService.getByUserId(user.getId());

        if (Objects.isNull(team))
            throw new ResourceNotFoundException("Team not found! Please add team before transfer!");

        if (team.getId() != player.getTeamId())
            throw new BadRequestException("The user can only transfer his own player!");

        transfer = transferMapper.toTransfer(transferDTO);
        transfer.setTeamId(team.getId());
        transfer.setTransferStartDate(new Date());
        Transfer savedTransfer = transferRepository.save(transfer);
        return transferMapper.toTransferDTO(savedTransfer);
    }

    @Override
    @Transactional
    public PlayerDTO buyPlayer(Long playerId) {

        boolean isAdmin = jwtUtil.getIsAdminFromToken();

        if (isAdmin) {
            throw new BadRequestException("Admin cannot buy players!");
        }

        Transfer transfer = transferRepository.findByPlayerId(playerId);

        if (Objects.isNull(transfer))
            throw new BadRequestException("Player is not available on transfer list!");

        User user = userService.getByEmail(jwtUtil.getUsernameFromToken());

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found!");

        TeamDTO ownTeam = teamService.getByUserId(user.getId());

        if (Objects.isNull(ownTeam))
            throw new ResourceNotFoundException("Team not found! Please add team before transferé");

        if (transfer.getTeamId() == ownTeam.getId())
            throw new BadRequestException("The player is already in your team!");

        if (ownTeam.getBalance() < transfer.getTransferAmount())
            throw new BadRequestException("You don't have enough money to transfer players.");

        PlayerDTO player = playerService.getById(playerId);

        //Update player old team 
        TeamDTO playerOldTeam = teamService.getByTeamId(player.getTeamId());
        playerOldTeam.setPlayerValue(playerOldTeam.getPlayerValue() - player.getMarketValue());
        playerOldTeam.setBalance(playerOldTeam.getBalance() + player.getMarketValue());
        teamService.update(playerOldTeam);

        //Update player information
        int increasedPlayerValue = (100 - 10 + 1) + 10;
        player.setMarketValue(player.getMarketValue() + increasedPlayerValue);
        player.setTeamId(ownTeam.getId());
        player = playerService.update(player);

        //Update own team
        ownTeam.setBalance(ownTeam.getBalance() - transfer.getTransferAmount());
        ownTeam.setPlayerValue(ownTeam.getPlayerValue() + player.getMarketValue());
        teamService.update(ownTeam);

        //Delete player from transfer
        delete(transfer.getId());

        return player;
    }

    @Override
    @Transactional
    public void delete(long id) {
        boolean exists = transferRepository.existsById(id);
        if (!exists)
            throw new ResourceNotFoundException("Transfer doesn't exists with given id!");
        transferRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<TransferDTO> get(TransferFilterDTO transferFilterDTO) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> criteriaQuery = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> itemRoot = criteriaQuery.from(Transfer.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (Objects.nonNull(transferFilterDTO.getTeamName())
            && transferFilterDTO.getTeamName() != "") {
            predicates.add(criteriaBuilder
                            .like(criteriaBuilder.lower(itemRoot.get("team").get("name")),
                                  "%" + transferFilterDTO.getTeamName() + "%"));
        }

        if (Objects.nonNull(transferFilterDTO.getCountry())
            && transferFilterDTO.getCountry() != "") {
            predicates.add(criteriaBuilder
                            .like(criteriaBuilder.lower(itemRoot.get("player").get("country")),
                                  "%" + transferFilterDTO.getCountry() + "%"));
        }

        if (Objects.nonNull(transferFilterDTO.getPlayerName())
            && transferFilterDTO.getPlayerName() != "") {
            predicates.add(criteriaBuilder
                            .like(criteriaBuilder.lower(itemRoot.get("player").get("firstName")),
                                  "%" + transferFilterDTO.getPlayerName() + "%"));
        }

        if (Objects.nonNull(transferFilterDTO.getPlayerValue())
            && transferFilterDTO.getPlayerValue() != "") {
            predicates.add(criteriaBuilder.equal(itemRoot.get("player").get("marketValue"),
                                                 transferFilterDTO.getPlayerValue()));
        }

        if (predicates.size() > 0)
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        List<Transfer> list = entityManager.createQuery(criteriaQuery).getResultList();
        return transferMapper.toTransferDTO(list);
    }

}
