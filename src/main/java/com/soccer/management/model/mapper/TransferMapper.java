package com.soccer.management.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import com.soccer.management.dto.TransferDTO;
import com.soccer.management.model.Transfer;

/**
 * @author enes.boyaci
 */
@Mapper(componentModel = "spring", uses = {PlayerMapper.class, TeamMapper.class})
public interface TransferMapper {
    TransferDTO toTransferDTO(Transfer transfer);

    Transfer toTransfer(TransferDTO transferDTO);

    @IterableMapping(qualifiedByName = {"toTransferDTO"})
    List<TransferDTO> toTransferDTO(Collection<Transfer> transfer);

}
