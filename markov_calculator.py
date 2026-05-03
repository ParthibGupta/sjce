import collections
import os

def calculate_markov_probabilities(log_file_path):
    if not os.path.exists(log_file_path):
        print(f"Log file {log_file_path} not found. Play the game to generate logs first.")
        return

    # Group states by session/user
    user_sessions = collections.defaultdict(list)
    
    # 1. Parse all events in the file
    with open(log_file_path, 'r') as file:
        for line in file:
            if not line.strip(): continue
            parts = [p.strip() for p in line.split('|')]
            if len(parts) >= 3:
                session_id = parts[1]
                state = parts[2]
                user_sessions[session_id].append(state)
                
    # 2 & 4. Collect names of states and keep count of transitions
    transitions = collections.defaultdict(lambda: collections.defaultdict(int))
    state_totals = collections.defaultdict(int)
    
    # 3. Repeat for each user session
    for session_id, states in user_sessions.items():
        for i in range(len(states) - 1):
            current_state = states[i]
            next_state = states[i+1]
            
            transitions[current_state][next_state] += 1
            state_totals[current_state] += 1

    # 5. Calculate possibilities (count / total) and print
    print("Markov Chain Transition Probabilities:\n")
    print(f"{'From State':<20} -> {'To State':<20} | Probability")
    print("-" * 65)
    
    for current_state, next_states in sorted(transitions.items()):
        total = state_totals[current_state]
        for next_state, count in sorted(next_states.items()):
            probability = count / total
            print(f"{current_state:<20} -> {next_state:<20} | {probability:.4f} ({count}/{total})")

if __name__ == "__main__":
    calculate_markov_probabilities('markov_states.log')