package app.repositories;

import app.entities.Task;


interface TaskRepositoryCustom
{

    Task saveFlushAndRefresh ( Task task );
}
