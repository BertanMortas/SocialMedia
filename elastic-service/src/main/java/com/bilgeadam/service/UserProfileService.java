package com.bilgeadam.service;

import com.bilgeadam.mapper.IElasticMapper;
import com.bilgeadam.rabbitmq.model.RegisterElasticModel;
import com.bilgeadam.repository.IUserProfileRepository;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService extends ServiceManager<UserProfile,String> {
    private final IUserProfileRepository repository;
    public UserProfileService(IUserProfileRepository repository) {
        super(repository);
        this.repository = repository;
    }
    public UserProfile createUserRabbitMq(RegisterElasticModel model){
        return save(IElasticMapper.INSTANCE.modelToUser(model));
    }
    // elasticte string değerler üzerinde herhangi bir sort işlemi yapılamamaktadır
    // bu işlem için elasticsearch arayüzünde kibana vb. araçlar ile ulaşarak bazı index ayarlarını
    // yapılması gerekmektedir bu sort işlemleri sayısal değerlerde çalışmaktadır.
    public Page<UserProfile> findAll(int currentPage, int size,String sortParameter,String sortDirection){
        Sort sort = null;
        Pageable pageable= null;
        if (sortParameter != null) {
            sort = Sort.by(Sort.Direction.fromString(sortDirection==null ? "ASC" : sortDirection),sortParameter);
        }
        if (sort != null) {
            pageable = PageRequest.of(currentPage,size == 0 ? 10: size,sort);
        }else {
            pageable = PageRequest.of(currentPage, size == 0 ? 10: size);
        }
        return repository.findAll(pageable);
    }

}
