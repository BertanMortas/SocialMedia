package com.bilgeadam.utility;

import com.bilgeadam.repository.entity.Base;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Getter
public class ServiceManager<T extends Base,ID> implements IService<T, ID> {
    private final ElasticsearchRepository<T,ID> jpaRepository;
    @Override
    public T save(T t) {
        t.setCreatedDate(System.currentTimeMillis());
        t.setUpdatedDate(System.currentTimeMillis());
        return jpaRepository.save(t);
    }

    @Override
    public Iterable<T> saveAll(Iterable<T> t) {
        t.forEach(x->{
            x.setCreatedDate(System.currentTimeMillis());
            x.setUpdatedDate(System.currentTimeMillis());
        });
        return jpaRepository.saveAll(t);
    }

    @Override
    public T update(T t) {
        t.setUpdatedDate(System.currentTimeMillis());
        return jpaRepository.save(t);
    }

    @Override
    public void delete(T t) {
        jpaRepository.delete(t);
    }

    @Override
    public void deleteById(ID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Iterable<T> findAll() {
        return jpaRepository.findAll();
    }
}
