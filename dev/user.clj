(ns user
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [pst doc find-doc]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [cheshire.core :as cheshire]))

;; What do I neede?
;;
;; - Run the same WebPPL program in parallel. Collect the results and process
;;   them further.
;;    - Collecting results should be easy, because WebPPL can print out JSON, if
;;      I understand it correctly.
;;    - Jobs specified as a map.
;;    - Job → Execution
;;    - Executions of different jobs can be mixed. If one execution of job A is
;;      still running, it should be possible to start executions of job B.
;;    - Also pass arguments to the WebPPL program. WebPPL can only understand
;;      numbers, strings, booleans, so we need to put in a look-up thing there.
;;    - Interface should be independent of whether we're running WebPPL or
;;      Anglican or whatever and where we're running.
;;    - I want to have four threads that fire off WebPPL processes.
;;
;; - Nice to have:
;;    - Integrate with Gorilla REPL.

(def job1 {::job/type ::job/webppl
           ::webppl/requires ["webppl-dp" "webppl-agents"]
           ::webppl/args {:softmax-noise 30
                          :prior "original"}})

(def job2 (assoc job1 ::webppl/args {:softmax-noise 1000
                                     :prior "optimistic"}) 
                                     

(def job1-res (run job1 {::n-exec 500 ::n-parallel 4}))
; In this case, WebPPL should run and return the results of 125 executions,
; because we don't want recompiles.


(defn keyword->ddash [k]
  (str "--" (name ddash)) 


(defn tolerantly-parse-json [s]
  (let [lines (string/split-lines s)
        parsed (map #(try (cheshire/parse-string s true)
                          (catch com.fasterxml.jackson.core.JsonParseException e
                            ::invalid-input)) 
                    lines)] 
    (remove #{::invalid-input} parsed) 



(defn webppl [path-to-script requires args]
  (let [require-args  (mapcat #(vec "--require" %) requires) 
        other-args    (mapcat #(vec (keyword->ddash (key %)) (val %)))
        
        {:keys [exit out err]}
        (apply shell/sh "webppl" path-to-script 
               (concat require-args other-args))] 
    (when (!= exit 0)
      (throw (ex-info "WebPPL script returned non-zero exit code."
                      {:exit exit :out out :err err})))
    (tolerantly-parse-json out))) 
 

(comment

  (def
    (shell/sh path-to-) 

)

(def job2-res (run job2 {::n-exec 250 ::n-parallel 4})) 

           
(average (map :return @job1-res)) 
(stddev (map :return @job1-res)) 
  ; WebPPL result automatically parsed.

(def noises-priors [[30   "original"]
                    [50   "original"]
                    [100  "original"]
                    [500  "original"]
                    [1000 "original"]
                    [1000 "optimistic"]])


(defn noise-prior-job [[noise prior]]
  (assoc job1 ::webppl/args {:softmax-noise noise
                             :prior prior}))

(s/def ::run-in (s/keys :req [::job ::exec]))
(s/def ::run-out (s/merge ::run-in (s/keys :req [::result])))

(s/fdef run
  :args (s/coll-of ::run-in)
  :ret (s/coll-of ::run-out))

(s/def ::sequential-numbers (s/coll-of number? :kind sequential?))
(s/def ::noises-priors-result (s/cat :trajectory ::sequential-numbers
                                     :regrets    ::sequential-numbers
                                     :rewards    ::sequential-numbers))

(run (map noise-prior-job noises-priors))

(s/def ::this-result (s/coll-of ::noises-priors-result)) 
  ; The results from all the executions.
  ; -> Compute total regret for every execution, then average over the total
  ;    regrets.


; [{::job … ::exec … ::result [noises-priors-result]}]

;              jobs            execs
(sr/transform [sr/ALL ::result s/ALL (sr/collect-one :regrets) ::total-regret]
              (fn [regrets _] (reduce + regrets))
              run-result)

(sr/transform [sr/ALL]) 
;; => {::job/type ::job/webppl ::webppl/args … ::job/result}


;; What does WebPPL return? {:trajectory [] :regrets [] :rewards []} 

      
     
;; AgentModels exercises: 
;; 
;; 1. Run Greedy Softmax agent with different softmax noise settings and priors.
;;    Compare total regret averaged over many episodes. Eg. 20 episodes of 500
;;    steps each; reset after every episode.
;;
;; 2. Set softmax noise to low and a false, non-optimistic prior. Will it ever
;;    converge to the optimal policy? (My idea: run for 10, 50, 500, 1000
;;    steps and see if average mean reward/regret increases.)
;;
;; 3. Compare Posterior Sampling and Softmax Greedy. Vary the armToCoinWeight
;;    parameter and number of arms, and compute the average and standard
;;    deviation of rewards averaged over many trials. 
