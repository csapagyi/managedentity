# Utility that supports entity management in JPA

It provides support for:
- (1) getting EntityManager and Session objects from JPA
- (2) secondary DB keys (a.k.a. business keys) while also using Ids as primary keys ([details of the issue and possible solutions here](http://lifeinacubicleblog.com/2016/10/09/jpa-the-uniqueness-dilemma-solution/))
- (3) paginating DB entries based on custom criteria

### API

### (1) public class DBAdapter

#### public static DBAdapter getInstance(String persistenceUnitName)
Returns DBAdapter object that uses 'persistenceUnitName' to get EntityManager and Session objects from JPA.
Needs to be called first to initialize 'persistenceUnitName'.

#### public static DBAdapter getInstance()
Returns DBAdapter object that uses previously specified persistence unit name.
Throws IllegalArgumentException if no persistence unit name was specified previously.

#### public Session getSession()
Returns Session object using SessionFactory's API.
'hibernate.current_session_context_class' property needs to be set in persistence.xml.

#### public Session getCurrentSession()
Returns Session object using SessionFactory's API.

### (2) public abstract class ManagedEntity
Superclass for entity classes.

#### public ManagedEntity merge()
Persists self if no persisted entity with the same secondary keys is already present.
Current session is queried from DBAdapter.
Returns persisted entity.

#### public ManagedEntity merge(Session session)
Persists self using provided 'session' if no persisted entity with the same secondary keys is already present.
Returns persisted entity.

#### public ManagedEntity update()
Updates self if no persisted entity with the same secondary keys is already present.
Current session is queried from DBAdapter.
Returns persisted entity.

#### public ManagedEntity update(Session session)
Updates self using provided 'session' if no persisted entity with the same secondary keys is already present.
Returns persisted entity.

#### protected abstract HashMap<String, ?> getEqualsCriteria()
Returns uniqueness criteria of self in a HashMap using Java attribute name as key. All child entities must implement it.

Example implementation:
```java
@Column(name = "name")
private String name;

@Column(name = "group_name")
private String groupName;

...

protected HashMap<String, ?> getEqualsCriteriaList() {
    HashMap<String, String> criteriaList = new HashMap<>();
    criteriaList.put("name", getName());
    criteriaList.put("groupName", getGroupName());

    return criteriaList;
}
```

### (3) public class Paginator
Notes:
- always specify an order
- entries are paginated in descending order (last one first), this is to ensure point (a) below

While iterating:
- (a) removing records that fit the pagination criteria may result in some records being returned multiple times but every record will be returned. With an idempotent implementation this will not cause any problems
- (b) adding records that fit the pagination criteria may result in some records not being returned therefore should be avoided
- (c) modifying records that fit the pagination criteria in a way that the criteria fields are modified may result in some records not being returned or being returned multiple times and therefore should be avoided

#### public Paginator(Criteria criteria, int paginateBy)
Returns Paginator object that will return entries matching 'criteria' by the number of 'paginateBy'.

#### public List<?> getNext()
Returns next chuck of data.

#### public List<?> getNext(Criteria criteria)
Returns next chuck of data using 'criteria' provided.
Should only be used if the original criteria's session may gets closed during Pagination. The same criteria must be provided as to the constructor.

#### public int getMaxResults()
Returns total number of records to be paginated.

## Feedback

Any feedback is much appreciated.

I can only tailor this project to fit use-cases I know about - which are usually my own ones. If you find that this might be the right direction to solve your problem too but you find that it's suboptimal or lacks features don't hesitate to contact me.

Please let me know if you make use of this project so that I can prioritize further efforts.
