package app.repositories;

import app.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


public class TaskRepositoryImpl implements TaskRepositoryCustom
{

    @Autowired
    private TaskRepository taskRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Task saveFlushAndRefresh ( Task task )
    {
        task = this.taskRepository.saveAndFlush( task );
        this.em.refresh( task );
        return task;
    }
}
