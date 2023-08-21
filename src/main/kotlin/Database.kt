import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import studio.hcmc.exposed.table.create
import studio.hcmc.exposed.transaction.Transaction
import studio.hcmc.exposed.transaction.TransactionCoroutineDispatcher
import studio.hcmc.exposed.transaction.suspendedTransaction
import java.util.*

object Database {
    class Builder private constructor(
        var url: String,
        var name: String,
        var propertiesName: String = "database.properties",
        var userPropertyName: String = "database.user",
        var passwordPropertyName: String = "database.password",
        var tables: List<Table> = emptyList(),
        var databaseConfiguration: DatabaseConfig.Builder.() -> Unit = {},
        var transactionConfiguration: TransactionCoroutineDispatcher.Builder.() -> Unit = {}
    ) {
        companion object {
            suspend operator fun invoke(
                url: String,
                name: String,
                configure: Builder.() -> Unit
            ): Database {
                return Builder(url, name)
                    .apply(configure)
                    .build()
            }
        }

        private suspend fun build(): Database {
            val properties = Properties()
            ClassLoader
                .getSystemResourceAsStream(propertiesName)
                .use { properties.load(it) }
            val user = properties.getProperty(userPropertyName)!!
            val password = properties.getProperty(passwordPropertyName)!!
            val database = Database.connect(
                url = "$url/$name",
                user = user,
                password = password,
                databaseConfig = DatabaseConfig {
                    useNestedTransactions = true
                    databaseConfiguration()
                }
            )

            TransactionManager.defaultDatabase = database
            TransactionCoroutineDispatcher.dispatcher = TransactionCoroutineDispatcher(transactionConfiguration)
            suspendedTransaction(context = Dispatchers.Transaction) {
                val sortedTables = SchemaUtils.sortTablesByReferences(tables)
                for (table in sortedTables) {
                    table.create(this)
                }
            }

            return database
        }
    }
}