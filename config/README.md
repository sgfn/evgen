# evgen configuration

Config files are stored in the YAML format

Names for keys should be self-explanatory

Everything that's not a variant must be an integer value

## Value constraints
 - **map_width, map_height**: must be between 1 and 50
 - **starting_animals**: must be between 0 and 100
 - **genome_length**: must be between 1 and 100
 - **min_mutations**: must be between 0 and genome_length
 - **max_mutations**: must be between min_mutations and genome_length
 - **starting_foliage, energy_gain, daily_foliage_growth, starting_energy, min_procreation_energy**: must be at least 0
 - **procreation_energy_loss**: must be between 0 and min_procreation_energy

### Warning
The above constraints are merely to assure the integrity of the simulation. They do not protect against nonsensical and useless setups, for instance:
 - daily_foliage_growth=0: plants will not grow
 - energy_gain=0: animals will be unable to gain energy
 - procreation_energy_loss=0: children will be born dead
 - min_procreation_energy=procreation_energy_loss: procreation may kill the parents
 - starting_animals<2, starting_energy=0: simulation will be extremely boring

## Available variants
 - **map\_variant**: globe, portal
 - **foliage\_variant**: equator, toxic
 - **mutation\_variant**: random, step
 - **behaviour\_variant**: predestined, crazy

## Default values
If loading of custom config fails, simulation will use default settings, which can be seen in the **Example config file** section below.

## Example config file
```
# This is an evgen configuration file.
# Refer to config/README.md for more info.

map_width: 20
map_height: 10
starting_foliage: 10
energy_gain: 5
daily_foliage_growth: 3
starting_animals: 5
starting_energy: 30
min_procreation_energy: 15
procreation_energy_loss: 10
min_mutations: 0
max_mutations: 5
genome_length: 10

map_variant: globe
foliage_variant: equator
mutation_variant: random
behaviour_variant: predestined
```
