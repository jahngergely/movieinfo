package hu.informula.movieinfo.persistence.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.informula.movieinfo.persistence.entity.RequestHistoryEntity;

@Repository
public interface RequestHistoryCrudService extends CrudRepository<RequestHistoryEntity, Long> {

}