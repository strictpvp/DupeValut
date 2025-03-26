package strictpvp.dupeVault

import kotlin.random.Random

class Utils {
    companion object {
        fun getRandom(chance: Int) : Boolean {
            return Random.nextInt(1, 100) <= chance
        }
    }
}